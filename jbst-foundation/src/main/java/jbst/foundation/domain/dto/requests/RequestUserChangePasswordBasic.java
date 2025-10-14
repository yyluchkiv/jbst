package jbst.foundation.domain.dto.requests;

import jbst.foundation.domain.base.Password;

public record RequestUserChangePasswordBasic(
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password newPassword,
        @Password.ValidPasswordNotBlank Password confirmPassword
) {

    public static RequestUserChangePasswordBasic hardcoded() {
        return new RequestUserChangePasswordBasic(
                Password.hardcoded(),
                Password.hardcoded()
        );
    }

    public static RequestUserChangePasswordBasic random() {
        return new RequestUserChangePasswordBasic(
                Password.random(),
                Password.random()
        );
    }

    public void assertPasswordsOrThrow() {
        this.newPassword.assertEqualsOrThrow(this.confirmPassword);
    }
}
