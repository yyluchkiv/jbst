package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;

public record JbstRequestUserEmail(
        @Email.ValidEmail Email email
) {

    public static JbstRequestUserEmail fixed() {
        return new JbstRequestUserEmail(
                Email.fixed()
        );
    }

}
