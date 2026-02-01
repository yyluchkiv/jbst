package jbst.foundation.feigns.slack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.*;
import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.time.JbstTimeAmount;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.feigns.slack.JbstSlack.ChatMessageReqType.MESSAGE_EDIT;
import static jbst.foundation.feigns.slack.JbstSlack.ChatMessageReqType.MESSAGE_SEND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSlack {

    // Classes: Definitions
    public interface SlackDefinition {
        @RequestLine("POST /chat.postMessage")
        @Headers(
                {
                        "Authorization: Bearer {token}",
                        "Content-Type: application/json; charset=utf-8"
                }
        )
        Response chatPostMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );

        @RequestLine("POST /chat.update")
        @Headers({
                "Authorization: Bearer {token}",
                "Content-Type: application/json; charset=utf-8"
        })
        Response chatUpdate(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Exception
    public static class ConfigurationException extends Exception {
        public ConfigurationException() {
            super("Please configure jbst-slack");
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
    public enum ChatMessageReqType {
        MESSAGE_SEND,
        MESSAGE_EDIT;

        public boolean isMessageSend() {
            return this == MESSAGE_SEND;
        }

        public boolean isMessageEdit() {
            return this == MESSAGE_EDIT;
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ChatMessageReq {
        @NotNull
        private final ChatMessageReqType type;
        @NotNull
        private final String channel;
        @NotNull
        private final String text;
        @Nullable
        private final MessageTs ts;

        public static ChatMessageReq messageSend(String channel, String text) {
            return new ChatMessageReq(MESSAGE_SEND, channel, text, null);
        }

        public static ChatMessageReq messageEdit(String channel, String text, MessageTs ts) {
            return new ChatMessageReq(MESSAGE_EDIT, channel, text, ts);
        }

        public void assertTypeMessageSend() throws ClientException {
            if (!type.isMessageSend()) {
                throw new ClientException("Expected req.type == MESSAGE_SEND");
            }
        }

        public void assertTypeMessageEdit() throws ClientException {
            if (!type.isMessageEdit()) {
                throw new ClientException("Expected req.type == MESSAGE_EDIT");
            }
        }

        public Map<String, Object> getReqBody() {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("channel", this.channel);
            reqBody.put("text", this.text);
            if (nonNull(this.ts)) {
                reqBody.put("ts", ts);
            }
            return reqBody;
        }
    }

    // Classes: Responses
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
    public static class ChatMessageRes {
        public Boolean ok;
        public String error;
        public String channel;
        public MessageTs ts;
        public Message message;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {
            public String type;
            public String subtype;
            public String text;
            public String ts;
            public String username;
            public String botId;
        }

        public void assertOK() throws ClientException {
            if (!this.ok) {
                throw new ClientException("Slack API response is not OK");
            }
        }
    }

    public record MessageDetailsRes(MessageTs ts, HeadersRes headers) {}

    // Constants
    private static final ObjectMapper OM = new ObjectMapper();

    // State
    private final AtomicBoolean configured = new AtomicBoolean(false);
    private final AtomicReference<Configuration> configurationAR = new AtomicReference<>();
    private final AtomicBoolean rateLimits = new AtomicBoolean(false);
    // State: queue-send
    private BlockingQueue<ChatMessageReq> sendQueue = new LinkedBlockingQueue<>(100);
    // State: queue-edit
    private BlockingQueue<MessageTs> editQueue = new LinkedBlockingQueue<>(100);
    private final Map<MessageTs, ChatMessageReq> editRequests = new ConcurrentHashMap<>();
    private final Set<MessageTs> editPendingIds = ConcurrentHashMap.newKeySet();

    // Definitions
    private final SlackDefinition definition;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    public final void configure(Configuration slackConfiguration) {
        if (this.configured.get()) {
            return;
        }
        this.configured.compareAndSet(false, true);
        this.configurationAR.set(slackConfiguration);
        this.sendQueue = new LinkedBlockingQueue<>(slackConfiguration.queueCapacity);
        this.editQueue = new LinkedBlockingQueue<>(slackConfiguration.queueCapacity);
    }

    @SuppressWarnings("unused")
    public final void configurePragmatic(String token) {
        this.configure(Configuration.pragmatic(token));
    }

    @SuppressWarnings("unused")
    @JbstDevelopmentOnly
    public final void configureHardcodedSleepDelay(String token) {
        this.configure(Configuration.developmentOnly(token));
    }

    @SuppressWarnings({"BusyWait", "ExtractMethodRecommender"})
    public final void start() {
        if (!this.configured.get()) {
            return;
        }
        var workerSend = new Thread(() -> {
            while (true) {
                ChatMessageReq req;
                try {
                    req = this.sendQueue.take();
                    this.messageSend(req);
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
                    this.incidentsPublisher.publishThrowable(ex3);
                }
            }
        }, "jbst-slack-send");
        workerSend.setDaemon(true);
        workerSend.start();

        var workerEdit = new Thread(() -> {
            while (true) {
                MessageTs messageTs;
                ChatMessageReq req;
                try {
                    messageTs = this.editQueue.take();
                    this.editPendingIds.remove(messageTs);
                    req = this.editRequests.remove(messageTs);
                    if (nonNull(req)) {
                        this.messageEdit(req);
                        sleep(this.configurationAR.get().sleepDelay.toMillis());
                    }
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
                    this.incidentsPublisher.publishThrowable(ex3);
                }
            }
        }, "jbst-slack-edit");
        workerEdit.setDaemon(true);
        workerEdit.start();

    }

    public final MessageDetailsRes messageSend(ChatMessageReq req) throws ConfigurationException, RateLimitsException, ClientException {
        this.assertConfigured();
        req.assertTypeMessageSend();
        try {
            var response = this.definition.chatPostMessage(this.configurationAR.get().token, req.getReqBody());
            var headers = this.assertHeaders(response);
            var res = OM.readValue(response.body().asInputStream(), ChatMessageRes.class);
            response.close();
            res.assertOK();
            return new MessageDetailsRes(res.ts, headers);
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_IO, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (FeignException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_FALLBACK, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        }
    }

    public final MessageDetailsRes messageEdit(ChatMessageReq req) throws ConfigurationException, RateLimitsException, ClientException {
        this.assertConfigured();
        req.assertTypeMessageEdit();
        try {
            var response = this.definition.chatUpdate(this.configurationAR.get().token, req.getReqBody());
            var headers = this.assertHeaders(response);
            var res = OM.readValue(response.body().asInputStream(), ChatMessageRes.class);
            response.close();
            res.assertOK();
            return new MessageDetailsRes(res.ts, headers);
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_RETRY, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (IOException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_IO, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        } catch (FeignException ex) {
            LOGGER.warn(JbstConstants.Logs.FEIGN_EXCEPTION_FALLBACK, "Slack", ex.getMessage());
            throw new ClientException(ex.getMessage());
        }
    }

    public final void submitMessage(ChatMessageReq req) {
        try {
            this.assertConfigured();
        } catch (ConfigurationException ex) {
            this.incidentsPublisher.publishThrowable(ex);
            return;
        }
        if (this.rateLimits.get() && req.type.isMessageEdit()) {
            this.incidentsPublisher.publishThrowable(new IllegalStateException("rate limits → dropping request (edit)"));
            return;
        }
        if (req.type.isMessageSend()) {
            var success = this.sendQueue.offer(req);
            if (!success) {
                this.incidentsPublisher.publishThrowable(new IllegalStateException("queue full → dropping request (send)"));
            }
        } else {
            var ts = req.getTs();
            if (isNull(ts)) {
                this.incidentsPublisher.publishThrowable(new IllegalStateException("unexpected request → request has no ts (edit)"));
                return;
            }
            this.editRequests.put(ts, req);
            if (this.editPendingIds.add(ts)) {
                var success = this.editQueue.offer(ts);
                if (!success) {
                    this.editPendingIds.remove(ts);
                    this.incidentsPublisher.publishThrowable(new IllegalStateException("queue full → dropping request (edit)"));
                }
            }
        }
    }

    public final void submitMessages(List<ChatMessageReq> reqs) {
        reqs.forEach(this::submitMessage);
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

    private HeadersRes assertHeaders(Response response) throws RateLimitsException, ClientException {
        var headers = new HeadersRes(response.headers());
        headers.assertRateLimits();
        return headers;
    }
}
