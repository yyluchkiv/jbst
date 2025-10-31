package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.JbstUserTokenType;

public record RequestUserToken(
        Email email,
        JbstUserTokenType type
) {

    public static RequestUserToken hardcoded() {
        return new RequestUserToken(
                Email.hardcoded(),
                JbstUserTokenType.EMAIL_CONFIRMATION
        );
    }
}
