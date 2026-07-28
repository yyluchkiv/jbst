package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Password;

public record JbstRequestUserChangePasswordBasic(
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password newPassword,
        @Password.ValidPasswordNotBlank Password confirmPassword
) {

    public static JbstRequestUserChangePasswordBasic fixed() {
        return new JbstRequestUserChangePasswordBasic(
                Password.fixed(),
                Password.fixed()
        );
    }

    public static JbstRequestUserChangePasswordBasic random() {
        return new JbstRequestUserChangePasswordBasic(
                Password.random(),
                Password.random()
        );
    }

    public void assertPasswordsOrThrow() {
        this.newPassword.assertEqualsOrThrow(this.confirmPassword);
    }
}
