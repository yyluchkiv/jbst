package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;

public record JbstRequestUserRegistrationMagicLink(
        @Email.ValidEmail Email email
) {

    public static JbstRequestUserRegistrationMagicLink fixed() {
        return new JbstRequestUserRegistrationMagicLink(
                Email.fixed()
        );
    }

    public static JbstRequestUserRegistrationMagicLink random() {
        return new JbstRequestUserRegistrationMagicLink(
                Email.random()
        );
    }

    public JbstRequestUserToken asRequestUserToken() {
        return new JbstRequestUserToken(
                this.email,
                JbstUserTokenType.MAGICLINK
        );
    }
}
