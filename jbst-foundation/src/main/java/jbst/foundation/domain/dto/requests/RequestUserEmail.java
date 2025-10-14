package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Email;

public record RequestUserEmail(
        @Email.ValidEmail Email email
) {

    public static RequestUserEmail hardcoded() {
        return new RequestUserEmail(
                Email.hardcoded()
        );
    }

}
