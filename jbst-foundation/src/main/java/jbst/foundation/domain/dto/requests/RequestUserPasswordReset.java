package jbst.foundation.domain.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Password;

import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;

public record RequestUserPasswordReset(
        @NotNull @NotEmpty String token,
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password newPassword,
        @Password.ValidPasswordNotBlank Password confirmPassword
) {

    public static RequestUserPasswordReset hardcoded() {
        return new RequestUserPasswordReset(
                "V2orWAWX4xlvam9V7u5aUqpgriM6qd8qRsgGyqNw",
                Password.hardcoded(),
                Password.hardcoded()
        );
    }

    public static RequestUserPasswordReset random() {
        return new RequestUserPasswordReset(
                randomStringLetterOrNumbersOnly(36),
                Password.random(),
                Password.random()
        );
    }

    public void assertPasswordsOrThrow() {
        this.newPassword.assertEqualsOrThrow(this.confirmPassword);
    }
}
