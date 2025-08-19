package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;

public record RequestMagicLinkEmail(
        @Schema(type = "string")
        @NotNull Email email
) {

    public static RequestMagicLinkEmail hardcoded() {
        return new RequestMagicLinkEmail(
                Email.hardcoded()
        );
    }

    public static RequestMagicLinkEmail random() {
        return new RequestMagicLinkEmail(
                Email.random()
        );
    }
}