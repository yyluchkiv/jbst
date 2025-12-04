package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;

public record JbstRequestUserToken(
        Email email,
        JbstUserTokenType type
) {

    public static JbstRequestUserToken hardcoded() {
        return new JbstRequestUserToken(
                Email.hardcoded(),
                JbstUserTokenType.EMAIL_CONFIRMATION
        );
    }
}
