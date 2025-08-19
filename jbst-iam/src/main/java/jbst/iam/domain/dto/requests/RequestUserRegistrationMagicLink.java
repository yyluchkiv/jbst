package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;

public record RequestUserRegistrationMagicLink(
        @Email.ValidEmail Email email
) {

    public static RequestUserRegistrationMagicLink hardcoded() {
        return new RequestUserRegistrationMagicLink(
                Email.hardcoded()
        );
    }

    public static RequestUserRegistrationMagicLink random() {
        return new RequestUserRegistrationMagicLink(
                Email.random()
        );
    }
}
