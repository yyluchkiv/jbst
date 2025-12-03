package jbst.foundation.feigns.openai;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstOpenai {

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

    // Classes: Requests
    public record OpenaiCompletionsRequest(
            String model,
            String prompt,
            @JsonProperty("max_tokens") int maxTokens,
            double temperature
    ) {

        @SuppressWarnings("unused")
        public static OpenaiCompletionsRequest davinci003(String prompt) {
            return new OpenaiCompletionsRequest(
                    "text-davinci-003",
                    prompt,
                    4000,
                    1.0d
            );
        }
    }

    // Classes: Responses
    public record OpenaiCompletionsResponse(
            String id,
            String object,
            long created,
            String model,
            List<OpenaiCompletionsChoiceResponse> choices,
            OpenaiCompletionsUsageResponse usage
    ) {
    }

    public record OpenaiCompletionsChoiceResponse(
            String text,
            int index,
            String logprobs,
            @JsonProperty("finish_reason")
            String finishReason
    ) {
    }

    public record OpenaiCompletionsUsageResponse(
            @JsonProperty("prompt_tokens")
            int promptTokens,
            @JsonProperty("completion_tokens")
            int completionTokens,
            @JsonProperty("total_tokens")
            int totalTokens
    ) {
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
