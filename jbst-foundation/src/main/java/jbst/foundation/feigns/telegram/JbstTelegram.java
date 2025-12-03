package jbst.foundation.feigns.telegram;

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
public class JbstTelegram {

    // Classes: Definitions
    public interface TelegramDefinition {
        @RequestLine("POST /bot{token}/sendMessage")
        @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
        void sendMessage(
                @Param("token") String token,
                @RequestBody Map<String, Object> requestBody
        );
    }

    // Classes: Requests
    public record TelegramMessageRequest(
            String token,
            String chatId,
            String text
    ) {

        public Map<String, Object> getRequestBody() {
            return Map.of(
                    "chat_id", this.chatId,
                    "text", this.text,
                    "parse_mode", "HTML"
            );
        }
    }

    // Definitions
    private final TelegramDefinition definition;

    public final void sendMessage(TelegramMessageRequest request) {
        try {
            this.definition.sendMessage(
                    request.token(),
                    request.getRequestBody()
            );
        } catch (RetryableException ex) {
            LOGGER.warn(JbstConstants.Logs.SERVER_OFFLINE, "Telegram", ex.getMessage());
            throw new IllegalArgumentException(ex);
        }
    }
}
