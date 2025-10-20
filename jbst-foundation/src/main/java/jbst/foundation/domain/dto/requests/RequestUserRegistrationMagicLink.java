package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.UserTokenType;

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

    public RequestUserToken asRequestUserToken() {
        return new RequestUserToken(
                this.email,
                UserTokenType.MAGIC_LINK
        );
    }
}
