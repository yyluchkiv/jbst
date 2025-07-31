package jbst.foundation.feigns.slack;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.RetryableException;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

@Slf4j
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
        void sendMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
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

    private final BlockingQueue<SlackMessageRequest> queue = new LinkedBlockingQueue<>();

    // Definitions
    private final SlackDefinition definition;
    // Incidents
    private final IncidentPublisher incidentPublisher;

    public SlackClient(SlackDefinition definition, IncidentPublisher incidentPublisher) {
        // beans
        this.definition = definition;
        this.incidentPublisher = incidentPublisher;
        // queue
        var ses = Executors.newSingleThreadScheduledExecutor(r -> {
            var thread = new Thread(r, "jbst-slack-client");
            thread.setDaemon(true);
            return thread;
        });
        ses.scheduleWithFixedDelay(() -> {
            try {
                var request = queue.poll();
                if (nonNull(request)) {
                    this.sendMessage(request);
                }
            } catch (RuntimeException ex) {
                this.incidentPublisher.publishThrowable(ex);
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
    }

    public final void sendMessage(SlackMessageRequest request) {
        try {
            this.definition.sendMessage(
                    request.token(),
                    request.getRequestBody()
            );
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.SERVER_OFFLINE, "Slack", ex.getMessage());
            throw new IllegalArgumentException(ex);
        }
    }

    public final void submitMessage(SlackMessageRequest request) {
        var success = this.queue.offer(request);
        if (!success) {
            this.incidentPublisher.publishThrowable(new IllegalStateException("slack-client queue is full"));
        }
    }

    public final void submitMessages(List<SlackMessageRequest> requests) {
        for (var request : requests) {
            this.submitMessage(request);
        }
    }
}
