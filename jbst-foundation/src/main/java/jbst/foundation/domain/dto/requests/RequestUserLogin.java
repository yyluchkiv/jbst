package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

public record RequestUserLogin(
        @Username.ValidUsername Username username,
        @Password.ValidPasswordNotBlank Password password
) {
    public static RequestUserLogin hardcoded() {
        return new RequestUserLogin(Username.hardcoded(), Password.hardcoded());
    }

    public static RequestUserLogin random() {
        return new RequestUserLogin(Username.random(), Password.random());
    }
}
