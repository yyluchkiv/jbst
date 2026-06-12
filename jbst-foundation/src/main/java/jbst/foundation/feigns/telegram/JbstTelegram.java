package jbst.foundation.feigns.telegram;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.time.JbstTimeAmount;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTelegram {

    // Classes: Definitions
    public interface TelegramDefinition {
        @RequestLine("POST /bot{token}/sendMessage")
        @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
        Response sendMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Exception
    public static class ConfigurationException extends Exception {
        public ConfigurationException() {
            super("Please configure jbst-telegram");
        }
    }

    @Getter
    public static class RateLimitsException extends Exception {
        private final JbstTimeAmount timeAmount;

        public RateLimitsException(Integer seconds) {
            super("Please wait %s seconds".formatted(seconds));
            this.timeAmount = new JbstTimeAmount(seconds, ChronoUnit.SECONDS);
        }
    }

    public static class ClientException extends Exception {
        public ClientException(String message) {
            super(message);
        }
    }

    // Classes: Base
    public record Configuration(String token, int queueCapacity, JbstTimeAmount sleepDelay) {
        public static Configuration pragmatic(String token) {
            return new Configuration(token, 100, new JbstTimeAmount(500, ChronoUnit.MILLIS));
        }

        @JbstDevelopmentOnly
        public static Configuration developmentOnly(String token) {
            return new Configuration(token, 25, new JbstTimeAmount(500, ChronoUnit.MILLIS));
        }
    }

    // Classes: Requests
    public record TelegramMessageRequest(
            String chatId,
            String text
    ) {
        public static TelegramMessageRequest of(String chatId, String text) {
            return new TelegramMessageRequest(chatId, text);
        }

        public Map<String, Object> getRequestBody() {
            return Map.of(
                    "chat_id", this.chatId,
                    "text", this.text,
                    "parse_mode", "HTML"
            );
        }
    }

    // Classes: Responses
    @SuppressWarnings("DuplicatedCode")
    public record HeadersRes(Map<String, Collection<String>> values) {
        public void assertRateLimits() throws RateLimitsException, ClientException {
            if (isNull(this.values)) {
                return;
            }
            var header = this.values.get("Retry-After");
            if (isEmpty(header)) {
                return;
            }
            try {
                var seconds = Integer.valueOf(header.iterator().next());
                throw new RateLimitsException(seconds);
            } catch (NumberFormatException ex) {
                throw new ClientException(ex.getMessage());
            }
        }
    }

    @SuppressWarnings("unused")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TelegramSendMessageRes {
        public Boolean ok;
        @JsonProperty("error_code")
        public Integer errorCode;
        public String description;
        public Parameters parameters;
        public Result result;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Parameters {
            @JsonProperty("retry_after")
            public Integer retryAfter;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Result {
            @JsonProperty("message_id")
            public Integer messageId;
        }

        public void assertRateLimits() throws RateLimitsException {
            if (nonNull(this.parameters) && nonNull(this.parameters.retryAfter)) {
                throw new RateLimitsException(this.parameters.retryAfter);
            }
        }

        public void assertOK() throws ClientException {
            if (isNull(this.ok) || !this.ok) {
                throw new ClientException(nonNull(this.description) ? this.description : "Telegram API response is not OK");
            }
        }
    }

    public record TelegramMessageDetailsRes(Integer messageId, HeadersRes headers) {}

    // Constants
    private static final ObjectMapper OM = new ObjectMapper();

    // State
    private final AtomicBoolean inited = new AtomicBoolean(false);
    private final AtomicReference<Configuration> configurationAR = new AtomicReference<>();
    private final AtomicBoolean rateLimits = new AtomicBoolean(false);
    // State: queue-send
    private BlockingQueue<TelegramMessageRequest> sendQueue = new LinkedBlockingQueue<>(100);

    // Definitions
    private final TelegramDefinition definition = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(JbstTelegram.TelegramDefinition.class, "https://api.telegram.org");

    public final void init(Configuration telegramConfiguration) {
        if (this.inited.get()) {
            return;
        }
        this.inited.compareAndSet(false, true);
        this.configurationAR.set(telegramConfiguration);
        this.sendQueue = new LinkedBlockingQueue<>(telegramConfiguration.queueCapacity);
    }

    @SuppressWarnings("unused")
    public final void initPragmatic(String token) {
        this.init(Configuration.pragmatic(token));
    }

    @SuppressWarnings("unused")
    @JbstDevelopmentOnly
    public final void initDevelopment(String token) {
        this.init(Configuration.developmentOnly(token));
    }

    @SuppressWarnings("unused")
    public final void reconfigure(Configuration telegramConfiguration) {
        this.configurationAR.set(telegramConfiguration);
    }

    @SuppressWarnings("BusyWait")
    public final void start() {
        if (!this.inited.get()) {
            return;
        }
        var workerSend = new Thread(() -> {
            while (true) {
                TelegramMessageRequest req;
                try {
                    req = this.sendQueue.take();
                    this.sendMessage(req);
                    sleep(this.configurationAR.get().sleepDelay.toMillis());
                } catch (InterruptedException ex1) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RateLimitsException ex2) {
                    this.rateLimits.set(true);
                    try {
                        Thread.sleep(ex2.timeAmount.toMillis());
                    } catch (InterruptedException ex21) {
                        Thread.currentThread().interrupt();
                        break;
                    } finally {
                        this.rateLimits.set(false);
                    }
                } catch (ConfigurationException | ClientException | RuntimeException ex3) {
                    LOGGER.error("jbst-telegram: send worker failure: {}", ex3.getMessage(), ex3);
                }
            }
        }, "jbst-telegram-send");
        workerSend.setDaemon(true);
        workerSend.start();
    }

    public final TelegramMessageDetailsRes sendMessage(TelegramMessageRequest req) throws ConfigurationException, RateLimitsException, ClientException {
        this.assertConfigured();
        try {
            var response = this.definition.sendMessage(this.configurationAR.get().token, req.getRequestBody());
            var headers = new HeadersRes(response.headers());
            var res = nonNull(response.body())
                    ? OM.readValue(response.body().asInputStream(), TelegramSendMessageRes.class)
                    : new TelegramSendMessageRes();
            response.close();
            headers.assertRateLimits();
            res.assertRateLimits();
            res.assertOK();
            return new TelegramMessageDetailsRes(nonNull(res.result) ? res.result.messageId : null, headers);
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "Telegram", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_IO, "Telegram", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (FeignException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_FALLBACK, "Telegram", ex.getMessage());
            throw new ClientException(ex.getMessage());
        }
    }

    public final void submitMessage(TelegramMessageRequest req) {
        try {
            this.assertConfigured();
        } catch (ConfigurationException ex) {
            LOGGER.error("jbst-telegram: {} → dropping request (send)", ex.getMessage());
            return;
        }
        var success = this.sendQueue.offer(req);
        if (!success) {
            LOGGER.warn("jbst-telegram: queue full → dropping request (send)");
        }
    }

    public final void submitMessages(List<TelegramMessageRequest> reqs) {
        reqs.forEach(this::submitMessage);
    }

    @SuppressWarnings("unused")
    public final boolean isRateLimited() {
        return this.rateLimits.get();
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void assertConfigured() throws ConfigurationException {
        if (!this.inited.get()) {
            throw new ConfigurationException();
        }
    }
}
