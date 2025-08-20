package jbst.iam.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.enums.UserTokenType;

public record RequestUserTokenMagicLink(
        @Email.ValidEmail Email email
) {

    public static RequestUserTokenMagicLink hardcoded() {
        return new RequestUserTokenMagicLink(
                Email.hardcoded()
        );
    }

    public static RequestUserTokenMagicLink random() {
        return new RequestUserTokenMagicLink(
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
