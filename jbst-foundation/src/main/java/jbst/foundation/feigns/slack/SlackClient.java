package jbst.foundation.feigns.slack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.RetryableException;
import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.time.TimeAmount;
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

import static jbst.foundation.utilities.concurrent.SleepUtility.sleepMilliseconds;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackClient {

    // Classes: Definitions
    public interface SlackDefinition {
        @RequestLine("POST /chat.postMessage")
        @Headers(
                {
                        "Authorization: Bearer {token}",
                        "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
                }
        )
        JbstSlackSendMessageRes chatPostMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );

        @RequestLine("POST /chat.update")
        @Headers({
                "Authorization: Bearer {token}",
                "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
        })
        JbstSlackSendMessageRes chatUpdate(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Exception
    public static class JbstSlackException extends Exception {
        public JbstSlackException(String message) {
            super(message);
        }
    }

    // Classes: Base
    public record JbstSlackConfiguration(String token, TimeAmount sleepDelay) { }

    public record JbstSlackMessageTs(@NotNull String value) {
        @JsonCreator
        public static JbstSlackMessageTs of(String value) {
            return new JbstSlackMessageTs(value);
        }

        @NotNull
        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }
    }

    // Classes: Requests
    public record JbstSlackChatMessage(String channel, String text) {

        public Map<String, Object> getReqBody() {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("channel", this.channel);
            reqBody.put("text", this.text);
            return reqBody;
        }
    }

    // Classes: Responses
    public record JbstSlackSendMessageRes(
            boolean ok,
            String error,
            String channel,
            JbstSlackMessageTs ts,
            Map<String, Object> message
    ) {
        public void assertOK() throws JbstSlackException {
            if (!this.ok) {
                throw new JbstSlackException("Slack API response is not OK");
            }
        }
    }

    private final AtomicBoolean configured = new AtomicBoolean(false);
    private final AtomicReference<String> token = new AtomicReference<>(null);
    private final BlockingQueue<JbstSlackChatMessage> queue = new LinkedBlockingQueue<>();

    // Definitions
    private final SlackDefinition definition;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    public final void configure(JbstSlackConfiguration slackConfiguration) {
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
                } catch (JbstSlackException | RuntimeException ex) {
                    this.incidentsPublisher.publishThrowable(ex);
                }
            }
        }, "jbst-slack-client");
        worker.setDaemon(true);
        worker.start();
    }

    @JbstDevelopmentOnly
    public final void configureHardcodedSleepDelay(String token) {
        this.configure(new JbstSlackConfiguration(token, new TimeAmount(500, ChronoUnit.MILLIS)));
    }

    public final JbstSlackMessageTs sendMessage(JbstSlackChatMessage req) throws JbstSlackException {
        this.assertConfigured();
        try {
            var res = this.definition.chatPostMessage(this.token.get(), req.getReqBody());
            res.assertOK();
            return res.ts;
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.SERVER_OFFLINE, "Slack", ex.getMessage());
            throw new JbstSlackException(ex.getMessage());
        }
    }

    public final JbstSlackMessageTs editMessage(JbstSlackMessageTs ts, JbstSlackChatMessage req) throws JbstSlackException {
        this.assertConfigured();
        try {
            var reqBody = req.getReqBody();
            reqBody.put("ts", ts);
            var res = this.definition.chatUpdate(this.token.get(), reqBody);
            res.assertOK();
            return res.ts;
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.SERVER_OFFLINE, "Slack", ex.getMessage());
            throw new JbstSlackException(ex.getMessage());
        }
    }

    public final void submitMessage(JbstSlackChatMessage req) {
        try {
            this.assertConfigured();
        } catch (JbstSlackException ex) {
            this.incidentsPublisher.publishThrowable(ex);
            return;
        }
        var success = this.queue.offer(req);
        if (!success) {
            this.incidentsPublisher.publishThrowable(new IllegalStateException("jbst-slack-client queue is full"));
        }
    }

    public final void submitMessages(List<JbstSlackChatMessage> reqs) {
        for (var request : reqs) {
            this.submitMessage(request);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void assertConfigured() throws JbstSlackException {
        if (!this.configured.get()) {
            var message = "Please configure jbst-slack-client";
            LOGGER.warn(message);
            throw new JbstSlackException(message);
        }
    }
}
