package jbst.iam.domain.dto.requests;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.iam.domain.enums.UserTokenType;

public record RequestUserToken(
        Email email,
        Username username,
        UserTokenType type
) {

    public static RequestUserToken hardcoded() {
        return new RequestUserToken(
                Email.hardcoded(),
                Username.hardcoded(),
                UserTokenType.EMAIL_CONFIRMATION
        );
    }
}
