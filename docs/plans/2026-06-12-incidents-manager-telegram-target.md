# Plan: `JbstIncidentsServiceTargetTelegram` — push incidents directly to Telegram

## Context

Today the incidents-manager has exactly one delivery target: `type: SERVER`, which POSTs every
incident over Feign (`JbstIncidentClientDefinition`) to a **separate standalone jbst-server-ops
instance** secured with basic-auth credentials. Running and maintaining that standalone server is
heavy for projects that only want a lightweight notification channel.

We want a second target, `type: TELEGRAM`, so an application can push incidents **directly** to a
Telegram channel/chat — no standalone incident server required. The Telegram HTTP infrastructure
already exists in jbst-foundation (`JbstTelegram` + `JbstConfigurationFeignClientTelegram`,
currently unused in production), so this is mostly wiring a new `JbstIncidentClientDefinition`
implementation and adding type-conditional configuration/validation. **Emails are out of scope.**

Target configuration:
```yaml
jbst:
  incidents-manager:
    enabled: true
    type: TELEGRAM
    telegram:
      token: "<bot-token>"
      chat-id: "<chat-id>"
```
The existing `SERVER` target stays fully supported (this **adds** a target, it does not replace one).

### Confirmed decisions
- **Config shape:** nested `telegram:` block (mirrors the existing `remote-server:` block).
- **Class name:** `JbstIncidentsServiceTargetTelegram` (implements `JbstIncidentClientDefinition`).
- **Message format:** plain-text `key — value` lines (no HTML styling / no escaping).

## Design overview

The existing incident flow is unchanged up to the client seam:

```
events/handlers → JbstIncidentsPublisher → ApplicationEventPublisher
  → JbstIncidentsSubscriber → JbstIncidentClient.registerIncident(incident)
    → JbstIncidentClientDefinition  ← the swappable target (the only thing we extend)
```

`JbstIncidentClientDefinition` already has two implementations selected by config: the Feign
`SERVER` proxy (enabled) and `JbstIncidentClientDefinitionSlf4J` (disabled fallback). We add a
**third** implementation, `JbstIncidentsServiceTargetTelegram`, and make
`JbstConfigurationIncidents` choose the implementation by `incidents-manager.type`.

## Changes (jbst-foundation/src/main)

### 1. Enum — add `TELEGRAM`
`jbst-foundation/src/main/java/jbst/foundation/domain/enums/JbstIncidentsManagerType.java`
```java
public enum JbstIncidentsManagerType {
    SERVER,
    TELEGRAM;   // new
    // hardcoded() stays SERVER; random() unchanged
}
```

### 2. New nested property — `JbstPropertyIncidentsManagerTelegram` (LEAF)
New file `domain/properties/configs/JbstPropertyIncidentsManagerTelegram.java`, modeled exactly on
`domain/properties/base/JbstPropertyRemoteServer.java`:
- Fields: `@JbstPropertyMandatory String token`, `@JbstPropertyMandatory String chatId`.
- `getNodeType() = LEAF`, `isToggle() = false`, `getNameNonLeaf() = JbstConstants.Symbols.DASH`.
- `hardcoded()` / `random()` factories (use `randomString()` for token/chatId).
- YAML `chat-id` binds to `chatId` via Spring relaxed binding (same as `base-url`/`baseURL`).

### 3. New target — `JbstIncidentsServiceTargetTelegram`
New file `incidents/feigns/definitions/JbstIncidentsServiceTargetTelegram.java`, sibling of
`incidents/feigns/definitions/JbstIncidentClientDefinitionSlf4J.java`,
`implements JbstIncidentClientDefinition`:
```java
@Slf4j
@RequiredArgsConstructor
public class JbstIncidentsServiceTargetTelegram implements JbstIncidentClientDefinition {
    private final JbstTelegram telegram;
    private final JbstPropertyIncidentsManagerTelegram properties;

    @Override
    public void registerIncident(JbstIncident incident) {
        try {
            this.telegram.sendMessage(TelegramMessageRequest.plain(
                    this.properties.getToken(),
                    this.properties.getChatId(),
                    incident.asText()            // see step 5
            ));
        } catch (Exception ex) {                  // mirror JbstIncidentClient's resilience
            LOGGER.error(SERVER_OFFLINE, "telegram", ex.getMessage());
            incident.print();
        }
    }
}
```
Failure behavior mirrors `incidents/feigns/clients/JbstIncidentClient.java`:
log via `JbstConstants.Logs.SERVER_OFFLINE` and fall back to `incident.print()` so an incident is
never lost if Telegram is unreachable.

### 4. Plain-text support — extend `TelegramMessageRequest`
In `jbst-foundation/src/main/java/jbst/foundation/feigns/telegram/JbstTelegram.java`,
`getRequestBody()` currently hard-codes `parse_mode = HTML`. Incident attributes (exception traces,
messages) can contain `<`, `>`, `&` that would break Telegram's HTML parser. Make parse-mode
optional, backward-compatibly:
```java
public record TelegramMessageRequest(String token, String chatId, String text, String parseMode) {
    public TelegramMessageRequest(String token, String chatId, String text) {  // existing callers → HTML
        this(token, chatId, text, "HTML");
    }
    public static TelegramMessageRequest plain(String token, String chatId, String text) {
        return new TelegramMessageRequest(token, chatId, text, null);            // no parse_mode
    }
    public Map<String, Object> getRequestBody() {
        var body = new HashMap<String, Object>();
        body.put("chat_id", this.chatId);
        body.put("text", this.text);
        if (nonNull(this.parseMode)) body.put("parse_mode", this.parseMode);
        return body;
    }
}
```
The existing 3-arg constructor keeps HTML (only the `@Disabled` `JbstTelegramTest` uses it).
`JbstTelegram` is otherwise unused in production, so this is safe.

### 5. Reusable incident text — `JbstIncident.asText()`
In `jbst-foundation/src/main/java/jbst/foundation/incidents/domain/JbstIncident.java`,
add a small method that reuses the existing `PRINT_COMPARATOR` (TYPE first, then key) so the
Telegram body matches `print()` ordering without duplicating sort logic:
```java
public String asText() {
    return this.attributes.entrySet().stream()
            .sorted(PRINT_COMPARATOR)
            .map(e -> e.getKey() + " — " + e.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
}
```

### 6. Type-conditional config & validation — `JbstPropertyIncidentsManager`
`domain/properties/configs/JbstPropertyIncidentsManager.java`:
- Add field `@JbstPropertyOptional private JbstPropertyIncidentsManagerTelegram telegram;`.
- **Change `remoteServer` from `@JbstPropertyMandatoryOnToggleEnabled` to `@JbstPropertyOptional`.**
  (Otherwise the annotation framework would force `remote-server` even for `type: TELEGRAM`.)
  Keep `type` as `@JbstPropertyMandatoryOnToggleEnabled`.
- Update the all-args constructor (now `enabled, type, remoteServer, telegram, incidents`) and the
  `hardcoded()` / `random()` / `disabled()` factories accordingly.
- Add a custom `assertProperties()` override following the precedent in
  `domain/properties/configs/JbstPropertySecurity.java`
  (call `super.assertProperties()`, then add checks):
```java
@Override
public void assertProperties() {
    super.assertProperties();                       // validates enabled + type, prints tree
    if (this.enabled) {
        if (this.type == SERVER) {
            assertNonNullOrThrow(this.remoteServer, "incidents-manager.remote-server is mandatory for type=SERVER");
            assertNonBlankOrThrow(this.remoteServer.getBaseURL(), "incidents-manager.remote-server.base-url is mandatory");
            assertNonNullOrThrow(this.remoteServer.getCredentials(), "incidents-manager.remote-server.credentials is mandatory");
        } else if (this.type == TELEGRAM) {
            assertNonNullOrThrow(this.telegram, "incidents-manager.telegram is mandatory for type=TELEGRAM");
            assertNonBlankOrThrow(this.telegram.getToken(), "incidents-manager.telegram.token is mandatory");
            assertNonBlankOrThrow(this.telegram.getChatId(), "incidents-manager.telegram.chat-id is mandatory");
        }
    }
}
```
(`assertNonNullOrThrow` / `assertNonBlankOrThrow` from `JbstAsserts`.) Leave the existing
`assertPropertiesExtended(...)` and `isEnabled(...)` methods untouched.

### 7. Bean wiring — `JbstConfigurationIncidents`
`jbst-foundation/src/main/java/jbst/foundation/configurations/JbstConfigurationIncidents.java`:
replace the single enabled `incidentClientDefinition()` bean with two `type`-conditional beans and
keep the slf4j fallback. Each target is isolated, so the SERVER path needs no `JbstTelegram`:
```java
@Bean
@ConditionalOnProperty(value = "jbst.incidents-manager.type", havingValue = "SERVER")
JbstIncidentClientDefinition incidentClientDefinitionServer() {
    var incidentServer = this.jbstProperties.getIncidentsManager().getRemoteServer();
    return Feign.builder()...target(JbstIncidentClientDefinition.class, incidentServer.getBaseURL()); // unchanged body
}

@Bean
@ConditionalOnProperty(value = "jbst.incidents-manager.type", havingValue = "TELEGRAM")
JbstIncidentClientDefinition incidentClientDefinitionTelegram(JbstTelegram telegramClient) {
    var telegram = this.jbstProperties.getIncidentsManager().getTelegram();
    return new JbstIncidentsServiceTargetTelegram(telegramClient, telegram);
}

@Bean
@ConditionalOnProperty(value = "jbst.incidents-manager.enabled", havingValue = "false", matchIfMissing = true)
JbstIncidentClientDefinition incidentClientDefinitionSlf4j() {  // unchanged
    return new JbstIncidentClientDefinitionSlf4J();
}
```
Exactly one definition bean is active in any valid config, so the existing
`incidentClient(JbstIncidentClientDefinition)` bean wires unambiguously. The `JbstTelegram` bean is
provided by `JbstConfigurationFeignClientTelegram` (same `jbst.foundation.configurations` package,
loaded together by the app's component scan). `@PostConstruct init()` runs `assertProperties()`
before the factory beans are built, so misconfiguration fails fast with a clear message.

## Tests (jbst-foundation/src/test)

**Constructor signature change** — update the 5-arg `new JbstPropertyIncidentsManager(...)` call
sites (add the new `telegram` arg):
- `domain/properties/configs/JbstPropertyIncidentsManager.java` factories (main).
- `domain/properties/utilties/PropertiesAsserterAndPrinterTest.java` (lines ~209, ~238).
- `incidents/services/JbstIncidentsPublisherTest.java` (line ~460).

**Bean-wiring tests** (these are white-box; they invoke `@Bean` methods by name):
- `JbstConfigurationIncidents1Test` (SERVER yml): rename references `incidentClientDefinition` →
  `incidentClientDefinitionServer` in `beansTests()` and `incidentClientDefinitionTest()`.
- `JbstConfigurationIncidents2Test` (disabled yml): update `beansTests()` method-name assertions;
  `incidentClientDefinitionTest()` now asserts `incidentClientDefinitionServer()` throws
  `NoSuchBeanDefinitionException`.
- **New** `JbstConfigurationIncidents3Test` + resource `tests-jbst-incidents-manager-03.yml`
  (`type: TELEGRAM` + `telegram.token`/`chat-id`): `@Import` both `JbstConfigurationIncidents` and
  `JbstConfigurationFeignClientTelegram`; assert the active definition is a
  `JbstIncidentsServiceTargetTelegram` and that the SERVER bean is absent.

**New unit tests:**
- `JbstIncidentsServiceTargetTelegramTest`: mock `JbstTelegram`, call `registerIncident(...)`,
  verify `sendMessage` is invoked with a `plain` request whose text equals `incident.asText()`;
  verify the catch path falls back to `incident.print()` on a thrown exception.
- `JbstPropertyIncidentsManagerTelegramTest`: mirror any existing `JbstPropertyRemoteServer` test
  (hardcoded/random/node-type).
- `JbstPropertyIncidentsManagerTest`: add a TELEGRAM-type `assertProperties()` happy-path plus a
  missing-`telegram` failure case; existing `disabledTest()` (`getRemoteServer()` null) still holds.
- Optionally add an `asText()` assertion to `JbstIncidentTest`.

## Example configuration

```yaml
jbst:
  incidents-manager:
    enabled: true
    type: TELEGRAM
    telegram:
      token: "123456:ABC-DEF..."
      chat-id: "-1001234567890"
    incidents:
      AUTHENTICATION_LOGIN: true
      REGISTER0: true
      # ... same per-incident toggle map as today
```

## Verification

1. **Compile:** `./compile-all.sh` (`mvn clean compile test-compile`).
2. **Unit tests:** `./junit-tests-only.sh` — all incidents/property/config tests green, including
   the new `JbstConfigurationIncidents3Test`, `JbstIncidentsServiceTargetTelegramTest`, and the
   updated constructor call sites.
3. **Negative validation:** a `type: TELEGRAM` config missing `telegram.token`/`chat-id` must fail
   startup via `assertProperties()` with the exact message; confirm in the new property test.
4. **Manual end-to-end (optional):** point a sample app at a real bot token + chat id, trigger an
   incident (e.g. a failed login), and confirm the plain-text `key — value` message arrives in the
   Telegram chat; then stop the network/use a bad token and confirm the incident still prints
   locally (fallback path).
5. **Full gate:** `mvn clean verify` before pushing.

## Out of scope / assumptions
- No email delivery (explicitly excluded).
- `SERVER` target and the standalone jbst-server-ops app are untouched and remain supported.
- No new Telegram HTTP client/config — reuse `JbstTelegram` + `JbstConfigurationFeignClientTelegram`.
- Plain-text messages (no HTML/Markdown); attributes rendered via `JbstIncident.asText()`.
