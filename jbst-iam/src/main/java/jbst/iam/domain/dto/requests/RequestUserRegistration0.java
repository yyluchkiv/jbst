package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.iam.domain.enums.UserTokenType;

import java.time.ZoneId;

import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static jbst.foundation.utilities.zones.ZonesUtility.reworkUkraineZoneId;

public record RequestUserRegistration0(
        @Email.ValidEmail Email email,
        @Username.ValidUsername Username username,
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password password,
        @Password.ValidPasswordNotBlank Password confirmPassword,
        @Schema(type = "string") @NotNull ZoneId zoneId
) {

    public static RequestUserRegistration0 hardcoded() {
        return new RequestUserRegistration0(
                Email.hardcoded(),
                Username.of("registration01"),
                Password.hardcoded(),
                Password.hardcoded(),
                randomZoneId()
        );
    }

    public static RequestUserRegistration0 random() {
        var password = Password.random();
        return new RequestUserRegistration0(
                Email.random(),
                Username.random(),
                password,
                password,
                randomZoneId()
        );
    }

    public void assertPasswordsOrThrow() {
        this.password.assertEqualsOrThrow(this.confirmPassword);
    }

    public RequestUserRegistration0 createReworkedUkraineZoneId() {
        return new RequestUserRegistration0(
                this.email,
                this.username,
                this.password,
                this.confirmPassword,
                reworkUkraineZoneId(this.zoneId)
        );
    }

    public RequestUserToken asRequestUserToken() {
        return new RequestUserToken(
                this.email,
                this.username,
                UserTokenType.EMAIL_CONFIRMATION
        );
    }
}
