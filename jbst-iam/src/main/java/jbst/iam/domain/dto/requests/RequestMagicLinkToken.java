package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RequestMagicLinkToken(
        @Schema(type = "string")
        @NotBlank String token
) {

    public static RequestMagicLinkToken hardcoded() {
        return new RequestMagicLinkToken("V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw");
    }

    public static RequestMagicLinkToken random() {
        return new RequestMagicLinkToken("randomToken123456789");
    }
}