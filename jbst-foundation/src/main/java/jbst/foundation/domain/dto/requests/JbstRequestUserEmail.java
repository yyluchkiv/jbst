package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;

public record JbstRequestUserEmail(
        @Email.ValidEmail Email email
) {

    public static JbstRequestUserEmail hardcoded() {
        return new JbstRequestUserEmail(
                Email.hardcoded()
        );
    }

}
