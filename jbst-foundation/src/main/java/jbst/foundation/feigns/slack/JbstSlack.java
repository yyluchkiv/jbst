package jbst.foundation.feigns.slack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import feign.*;
import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.development.JbstDevelopment;
import jbst.foundation.domain.time.JbstTimeAmount;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static jbst.foundation.domain.concurrent.JbstSleep.sleepMilliseconds;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSlack {

    // Classes: Definitions
    public interface SlackDefinition {
        @RequestLine("POST /chat.postMessage")
        @Headers(
                {
                        "Authorization: Bearer {token}",
                        "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
                }
        )
        SendMessageRes chatPostMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );

        @RequestLine("POST /chat.update")
        @Headers({
                "Authorization: Bearer {token}",
                "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
        })
        SendMessageRes chatUpdate(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Exception
    public static class ConfigurationException extends Exception {
        public ConfigurationException() {
            super("Please configure jbst-slack-client");
        }
    }

    public static class ClientException extends Exception {
        public ClientException(String message) {
            super(message);
        }
    }

    // Classes: Base
    public record Configuration(String token, JbstTimeAmount sleepDelay) { }

    public record MessageTs(@NotNull String value) {
        @JsonCreator
        public static MessageTs of(String value) {
            return new MessageTs(value);
        }

        @NotNull
        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }
    }

    // Classes: Requests
    public record ChatMessage(String channel, String text) {

        public Map<String, Object> getReqBody() {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("channel", this.channel);
            reqBody.put("text", this.text);
            return reqBody;
        }
    }

    // Classes: Responses
    public record SendMessageRes(
            boolean ok,
            String error,
            String channel,
            MessageTs ts,
            Map<String, Object> message
    ) {
        public void assertOK() throws ClientException {
            if (!this.ok) {
                throw new ClientException("Slack API response is not OK");
            }
        }
    }

    private final AtomicBoolean configured = new AtomicBoolean(false);
    private final AtomicReference<String> token = new AtomicReference<>(null);
    private final BlockingQueue<ChatMessage> queue = new LinkedBlockingQueue<>();

    // Definitions
    private final SlackDefinition definition;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    public final void configure(Configuration slackConfiguration) {
        if (this.configured.get()) {
            return;
        }
        this.configured.compareAndSet(false, true);
        this.token.set(slackConfiguration.token);
        var worker = new Thread(() -> {
            while (true) {
                try {
                    var request = this.queue.take();
                    this.sendMessage(request);
                    sleepMilliseconds(slackConfiguration.sleepDelay.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ConfigurationException | ClientException | RuntimeException ex) {
                    this.incidentsPublisher.publishThrowable(ex);
                }
            }
        }, "jbst-slack-client");
        worker.setDaemon(true);
        worker.start();
    }

    @SuppressWarnings("unused")
    @JbstDevelopmentOnly
    public final void configureHardcodedSleepDelay(String token) {
        this.configure(new Configuration(token, new JbstTimeAmount(500, ChronoUnit.MILLIS)));
    }

    public final MessageTs sendMessage(ChatMessage req) throws ConfigurationException, ClientException {
        this.assertConfigured();
        try {
            var res = this.definition.chatPostMessage(this.token.get(), req.getReqBody());
            res.assertOK();
            return res.ts;
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        }
    }

    public final MessageTs editMessage(MessageTs ts, ChatMessage req) throws ConfigurationException, ClientException {
        this.assertConfigured();
        try {
            var reqBody = req.getReqBody();
            reqBody.put("ts", ts);
            var res = this.definition.chatUpdate(this.token.get(), reqBody);
            res.assertOK();
            return res.ts;
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "Slack", ex.getMessage());
            // TODO [YYL-jbst] debuggable (array vs. object)
            LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
            LOGGER.error("Slack RetryableException Message: {}", ex.getMessage());
            LOGGER.error("Slack RetryableException Content: {}", ex.contentUTF8());
            LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
            throw new ClientException(ex.getMessage());
        } catch (FeignException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_FALLBACK, "Slack", ex.getMessage());
            // TODO [YYL-jbst] debuggable (array vs. object)
            LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
            LOGGER.error("Slack FeignException Message: {}", ex.getMessage());
            LOGGER.error("Slack FeignException Content: {}", ex.contentUTF8());
            LOGGER.error(JbstConstants.Symbols.LINE_SEPARATOR_INTERPUNCT);
            throw new ClientException(ex.getMessage());
        }
    }

    public final void submitMessage(ChatMessage req) {
        try {
            this.assertConfigured();
        } catch (ConfigurationException ex) {
            this.incidentsPublisher.publishThrowable(ex);
            return;
        }
        var success = this.queue.offer(req);
        if (!success) {
            this.incidentsPublisher.publishThrowable(new IllegalStateException("jbst-slack-client queue is full"));
        }
    }

    public final void submitMessages(List<ChatMessage> reqs) {
        for (var request : reqs) {
            this.submitMessage(request);
        }
    }

    // =================================================================================================================
    // STATIC
    // =================================================================================================================
    public static String getSlackTable(String header, String table) {
        return "```\n%s\n%s\n```".formatted(header, table);
    }

    public static String getSlackMessage(String text) {
        return "```\n%s\n```".formatted(text);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void assertConfigured() throws ConfigurationException {
        if (!this.configured.get()) {
            throw new ConfigurationException();
        }
    }
}
