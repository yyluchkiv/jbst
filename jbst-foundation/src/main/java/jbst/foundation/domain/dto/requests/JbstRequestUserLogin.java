package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

public record JbstRequestUserLogin(
        @Username.ValidUsername Username username,
        @Password.ValidPasswordNotBlank Password password
) {
    public static JbstRequestUserLogin fixed() {
        return new JbstRequestUserLogin(Username.fixed(), Password.fixed());
    }

    public static JbstRequestUserLogin random() {
        return new JbstRequestUserLogin(Username.random(), Password.random());
    }
}
