# Plan: Add JbstSlack-style rate limiting to JbstTelegram

## Context

`JbstTelegram` (`jbst-foundation/src/main/java/jbst/foundation/feigns/telegram/JbstTelegram.java`) is currently a thin, fire-and-forget Feign wrapper: one synchronous `sendMessage()` returning `void`, with no awareness of Telegram's HTTP 429 rate limiting. If the bot exceeds Telegram's limits (~30 msg/s globally, ~1 msg/s per chat), requests are silently lost and the only handling is rethrowing `RetryableException` as `IllegalArgumentException`.

`JbstSlack` (`jbst-foundation/src/main/java/jbst/foundation/feigns/slack/JbstSlack.java`) already solves this for Slack with a mature pattern: a `RateLimitsException` carrying a `JbstTimeAmount`, a `Retry-After` header parser, a `rateLimits` flag, an async in-memory queue drained by a daemon worker that sleeps-and-recovers on 429, and failure reporting through `JbstIncidentsPublisher`.

**Goal:** bring the same rate-limit machinery to `JbstTelegram` (full parity), so message sends are queued, paced, and automatically back off when Telegram returns 429 — instead of being dropped.

**Confirmed decisions (from clarifying questions):**
1. **Full parity** — replicate Slack's async queue + worker + lifecycle, not just sync detection.
2. **Header + JSON body fallback** — read the `Retry-After` header first (like Slack), then fall back to Telegram's response body `parameters.retry_after`.

`JbstTelegram` has **no callers anywhere** in the repo (only its Feign config + a `@Disabled` test), so reshaping its public API is low-risk.

> Note: there is also a separate `JbstRateLimiter` (Guava/Caffeine token-bucket, `jbst-foundation/src/main/java/jbst/foundation/domain/concurrent/JbstRateLimiter.java`). We are deliberately **not** using it — the request is to mirror the reactive, header-driven Slack pattern.

## Step 0 — Save this plan into the repo

`docs/plans/` does not exist yet. As the first execution step, create it and copy this plan to:
`docs/plans/2026-06-12-jbst-telegram-rate-limits.md`

## Design overview

Telegram has a single operation (`sendMessage`), so it maps to Slack's **single "send" path** — no "edit" path. We mirror Slack's structure but drop Slack-only concepts that don't apply:
- **Drop** the edit queue / `editRequests` / `editPendingIds` machinery (no edit endpoint).
- **Drop** `ChatMessageDestination.enabled` + `UnexpectedDisabledMessageReqException` (Telegram requests have no enabled/disabled destination concept).
- **Move the bot token into `Configuration`** (like Slack), so `TelegramMessageRequest` no longer carries it.

## Changes

### 1. `JbstTelegram` — the core rewrite
`jbst-foundation/src/main/java/jbst/foundation/feigns/telegram/JbstTelegram.java`

**a. Feign definition returns `feign.Response` (key enabler).** Change `TelegramDefinition.sendMessage` from `void` to `feign.Response`. With a raw `Response` return type, Feign returns the response as-is on 4xx (no ErrorDecoder/FeignException), so we can read the 429 `Retry-After` header and body — exactly how `SlackDefinition.chatPostMessage` returns `Response`.

**b. Exceptions** (mirror Slack, minus the edit-only one):
- `ConfigurationException extends Exception` — message `"Please configure jbst-telegram"`.
- `RateLimitsException extends Exception` with `@Getter` + `JbstTimeAmount timeAmount`; constructor `RateLimitsException(Integer seconds)` → message `"Please wait %s seconds"`, `timeAmount = new JbstTimeAmount(seconds, ChronoUnit.SECONDS)`. (Copied from `JbstSlack.RateLimitsException`.)
- `ClientException extends Exception`.

**c. `Configuration` record** (copied from Slack, holds the token):
```java
public record Configuration(String token, int queueCapacity, JbstTimeAmount sleepDelay) {
    public static Configuration pragmatic(String token) {
        return new Configuration(token, 100, new JbstTimeAmount(500, ChronoUnit.MILLIS));
    }
    @JbstDevelopmentOnly
    public static Configuration developmentOnly(String token) {
        return new Configuration(token, 25, new JbstTimeAmount(500, ChronoUnit.MILLIS));
    }
}
```

**d. Request model** — drop the `token` field:
```java
public record TelegramMessageRequest(String chatId, String text) {
    public static TelegramMessageRequest of(String chatId, String text) { ... }
    public Map<String, Object> getRequestBody() {
        return Map.of("chat_id", this.chatId, "text", this.text, "parse_mode", "HTML");
    }
}
```

**e. Response + rate-limit parsing** (header first, then body fallback — the confirmed choice):
- Reuse a `HeadersRes(Map<String, Collection<String>> values)` record with `assertRateLimits()` that reads the `Retry-After` header and throws `RateLimitsException(seconds)` — copied verbatim from `JbstSlack.HeadersRes`.
- Add `TelegramSendMessageRes` (Jackson, `@JsonIgnoreProperties(ignoreUnknown = true)`), using `@JsonProperty` for snake_case fields:
  - `Boolean ok`, `@JsonProperty("error_code") Integer errorCode`, `String description`, `Parameters parameters`, `Result result`.
  - `Parameters { @JsonProperty("retry_after") Integer retryAfter; }`
  - `Result { @JsonProperty("message_id") Integer messageId; }`
  - `void assertRateLimits() throws RateLimitsException` → if `parameters != null && parameters.retryAfter != null`, throw `RateLimitsException(parameters.retryAfter)`.
  - `void assertOK() throws ClientException` → if `ok` is null/false, throw `ClientException(description != null ? description : "Telegram API response is not OK")`.
- `record TelegramMessageDetailsRes(Integer messageId, HeadersRes headers) {}` — the synchronous return type (analogous to Slack's `MessageDetailsRes`, with `messageId` standing in for Slack's `ts`).

**f. State + lifecycle** (mirror Slack, single send path):
```java
private static final ObjectMapper OM = new ObjectMapper();
private final AtomicBoolean inited = new AtomicBoolean(false);
private final AtomicReference<Configuration> configurationAR = new AtomicReference<>();
private final AtomicBoolean rateLimits = new AtomicBoolean(false);
private BlockingQueue<TelegramMessageRequest> sendQueue = new LinkedBlockingQueue<>(100);
private final TelegramDefinition definition;          // existing
private final JbstIncidentsPublisher incidentsPublisher;  // new
```
- `init(Configuration)`, `initPragmatic(token)`, `@JbstDevelopmentOnly initDevelopment(token)`, `reconfigure(Configuration)` — copied from Slack (sizing `sendQueue` from `queueCapacity`).

**g. `start()`** — one daemon worker thread `"jbst-telegram-send"`, structurally identical to Slack's `workerSend`:
```
take() from sendQueue → sendMessage(req) → sleep(sleepDelay)
  catch InterruptedException → interrupt + break
  catch RateLimitsException  → rateLimits=true; sleep(timeAmount); finally rateLimits=false
  catch ConfigurationException | ClientException | RuntimeException → incidentsPublisher.publishThrowable(ex)
```
(On 429 the in-flight message is dropped after the back-off sleep — same as Slack. Re-enqueue is intentionally **not** added, to keep parity; noted as a possible future enhancement.)

**h. Synchronous `sendMessage(TelegramMessageRequest req)`** → returns `TelegramMessageDetailsRes`, throws `ConfigurationException, RateLimitsException, ClientException`. Mirrors `JbstSlack.messageSend`:
1. `assertConfigured()`.
2. Call `definition.sendMessage(configurationAR.get().token(), req.getRequestBody())`.
3. Build `HeadersRes` from `response.headers()`; read body into `TelegramSendMessageRes` (guard null body); `response.close()`.
4. Assert in order: `headers.assertRateLimits()` (header priority) → `res.assertRateLimits()` (body fallback) → `res.assertOK()`.
5. Return `new TelegramMessageDetailsRes(res.result != null ? res.result.messageId : null, headers)`.
6. Catch `RetryableException` / `IOException` / `FeignException` → log via `JbstConstants.Logs.FEIGN_EXCEPTION_{RETRY,IO,FALLBACK}` with `"Telegram"` and rethrow as `ClientException` (same as Slack).

**i. Async API** (mirror Slack):
- `submitMessage(TelegramMessageRequest req)`: `assertConfigured()` (on failure `publishThrowable` + return); `sendQueue.offer(req)`; if `false`, `publishThrowable(new IllegalStateException("queue full → dropping request (send)"))`. (No `rateLimits`/edit-drop branch — Telegram has only sends.)
- `submitMessages(List<TelegramMessageRequest> reqs)`: `reqs.forEach(this::submitMessage)`.

**j. Private helper:** `assertConfigured()` → throw `ConfigurationException` if `!inited.get()`.

### 2. Feign config — inject the incidents publisher
`jbst-foundation/src/main/java/jbst/foundation/configurations/JbstConfigurationFeignClientTelegram.java`

Mirror `JbstConfigurationFeignClientSlack`: add `@Import({ JbstConfigurationIncidents.class })`, inject `JbstIncidentsPublisher`, and pass it into `new JbstTelegram(telegramDefinition, this.incidentsPublisher)`. (`JbstConfigurationIncidents` provides the `incidentsPublisher` bean and pulls in `JbstProperties`.)

### 3. Test — match the Slack test shape
`jbst-foundation/src/test/java/jbst/foundation/feigns/telegram/JbstTelegramTest.java`

Mirror `JbstSlackTest`:
- `TestConfiguration` imports **both** `JbstConfigurationFeignClientTelegram` **and** `TestJbstConfigurationPropertiesHardcoded` (the latter now required, since the Telegram config imports `JbstConfigurationIncidents` → needs `JbstProperties`).
- `@Autowired` constructor calls `telegram.initPragmatic(TOKEN); telegram.start();`.
- Keep a `@Disabled sendMessage` test updated to the new `TelegramMessageRequest.of(chatId, text)` shape; add a `@Disabled` backpressure test using `submitMessages(...)` + `JbstSleep.sleep(...)`, paralleling `messagesBackpressureSend`.

These tests stay `@Disabled` (they hit the live Telegram API), exactly like the Slack tests — so they won't run in CI but document usage and compile-check the new API.

## Reused existing code (no new utilities)
- `JbstTimeAmount` (`domain/time/JbstTimeAmount.java`) — `toMillis()` for back-off; carried by `RateLimitsException`.
- `JbstIncidentsPublisher.publishThrowable(...)` (`incidents/services/JbstIncidentsPublisher.java`) — worker/submit failure reporting.
- `JbstConstants.Logs.FEIGN_EXCEPTION_{RETRY,IO,FALLBACK}` (`domain/constants/JbstConstants.java`).
- `@JbstDevelopmentOnly`, `JbstSleep`, `TestJbstConfigurationPropertiesHardcoded` — as in the Slack equivalents.

## Verification

1. **Compile:** `./compile-all.sh` (or `mvn -pl jbst-foundation clean test-compile`) — confirms the new API + updated test compile.
2. **Unit tests:** `./junit-tests-only.sh` — the Telegram tests remain `@Disabled`; verify nothing else in `jbst-foundation` breaks from the config/import change.
3. **Manual smoke (optional, local only):** temporarily un-`@Disable` `JbstTelegramTest.sendMessage`, set a real bot token + chat id, run it, confirm a message arrives and the method returns a `messageId`.
4. **Rate-limit path (optional, local only):** the `@Disabled` backpressure test floods `submitMessages(...)`; against a low limit you can observe the worker logging an incident and pausing ~`retry_after` seconds rather than erroring out.

## Out of scope / deliberate omissions
- No edit endpoint, no `enabled`-destination gating, no re-enqueue of the 429-triggering message (all to stay faithful to Slack's send path).
- No changes to `JbstRateLimiter` / `JbstRateLimitsService`.
- No new Spring auto-wiring of `JbstTelegram` into server modules (it currently has no callers; that's a separate task).
