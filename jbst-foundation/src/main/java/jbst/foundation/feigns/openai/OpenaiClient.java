package jbst.foundation.feigns.openai;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import jbst.foundation.feigns.openai.domain.requests.OpenaiCompletionsRequest;
import jbst.foundation.feigns.openai.domain.responses.OpenaiCompletionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@SuppressWarnings("unused")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OpenaiClient {

    // Classes: Definitions
    public interface OpenaiDefinition {
        @RequestLine("POST /v1/completions")
        @Headers(
                {
                        "Authorization: Bearer {token}",
                        "Content-Type: " + MediaType.APPLICATION_JSON_VALUE
                }
        )
        OpenaiCompletionsResponse completions(
                @Param("token") String token,
                OpenaiCompletionsRequest request
        );
    }

    // Definitions
    private final OpenaiDefinition definition;

    public final OpenaiCompletionsResponse getCompletions(String apiKey, OpenaiCompletionsRequest request) {
        return this.definition.completions(
                apiKey,
                request
        );
    }
}
