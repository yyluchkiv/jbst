package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record RequestMagicLinkToken(
        @Schema(type = "string")
        @NotBlank String token
) {

    public static RequestMagicLinkToken hardcoded() {
        return new RequestMagicLinkToken("E4944FFE506B2838A8F667D95C5FB28DB3ABAE54");
    }

    public static RequestMagicLinkToken random() {
        return new RequestMagicLinkToken(randomString());
    }
}
