package jbst.iam.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.enums.UserTokenType;

public record RequestUserToken(
        Email email,
        UserTokenType type
) {

    public static RequestUserToken hardcoded() {
        return new RequestUserToken(
                Email.hardcoded(),
                UserTokenType.EMAIL_CONFIRMATION
        );
    }
}
