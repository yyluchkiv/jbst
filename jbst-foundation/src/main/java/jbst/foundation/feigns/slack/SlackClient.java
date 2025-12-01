package jbst.foundation.feigns.slack;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.RetryableException;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
        SlackSendMessageResponse sendMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Exception
    public static class SlackConfigurationException extends Exception {
        public SlackConfigurationException(String message) {
            super(message);
        }
    }

    // Classes: Requests
    public record SlackMessageRequest(
            String token,
            String channel,
            String text
    ) {

        public Map<String, Object> getRequestBody() {
            return Map.of(
                    "channel", this.channel,
                    "text", this.text
            );
        }
    }

    // Classes: Responses
    public record SlackSendMessageResponse(
            boolean ok,
            String channel,
            String ts,
            Map<String, Object> message
    ) {}

    private final AtomicBoolean configured = new AtomicBoolean(false);
    private final BlockingQueue<SlackMessageRequest> queue = new LinkedBlockingQueue<>();

    // Definitions
    private final SlackDefinition definition;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    @SuppressWarnings("BusyWait")
    public final void configure(TimeAmount timeAmount) {
        if (this.configured.get()) {
            return;
        }
        this.configured.compareAndSet(false, true);
        var worker = new Thread(() -> {
            while (true) {
                try {
                    var request = this.queue.take();
                    this.sendMessage(request);
                    Thread.sleep(timeAmount.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (SlackConfigurationException | RuntimeException ex) {
                    this.incidentsPublisher.publishThrowable(ex);
                }
            }
        }, "jbst-slack-client");
        worker.setDaemon(true);
        worker.start();
    }

    public final void sendMessage(SlackMessageRequest request) throws SlackConfigurationException {
        this.assertConfigured();
        try {
            this.definition.sendMessage(
                    request.token(),
                    request.getRequestBody()
            );
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.SERVER_OFFLINE, "Slack", ex.getMessage());
            throw new SlackConfigurationException(ex.getMessage());
        }
    }

    public final void submitMessage(SlackMessageRequest request) throws SlackConfigurationException {
        this.assertConfigured();
        var success = this.queue.offer(request);
        if (!success) {
            this.incidentsPublisher.publishThrowable(new IllegalStateException("jbst-slack-client queue is full"));
        }
    }

    public final void submitMessages(List<SlackMessageRequest> requests) throws SlackConfigurationException {
        this.assertConfigured();
        for (var request : requests) {
            this.submitMessage(request);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void assertConfigured() throws SlackConfigurationException {
        if (!this.configured.get()) {
            var message = "Please configure jbst-slack-client";
            LOGGER.warn(message);
            throw new SlackConfigurationException(message);
        }
    }
}
