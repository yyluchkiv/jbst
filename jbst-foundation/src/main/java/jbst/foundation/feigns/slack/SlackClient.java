package jbst.foundation.feigns.slack;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.RetryableException;
import jbst.foundation.domain.constants.JbstConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

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

    // Definitions
    private final SlackDefinition definition;

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
}
