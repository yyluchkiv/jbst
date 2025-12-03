package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

import java.time.ZoneId;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record RequestUserRegistration1(
        @Username.ValidUsername Username username,
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password password,
        @Password.ValidPasswordNotBlank Password confirmPassword,
        @Schema(type = "string") @NotNull ZoneId zoneId,
        @NotBlank String code
) {

    public static RequestUserRegistration1 hardcoded() {
        return new RequestUserRegistration1(
                Username.of("registration11"),
                Password.hardcoded(),
                Password.hardcoded(),
                randomZoneId(),
                randomString()
        );
    }

    public void assertPasswordsOrThrow() {
        this.password.assertEqualsOrThrow(this.confirmPassword);
    }

    public RequestUserRegistration1 createReworkedUkraineZoneId() {
        return new RequestUserRegistration1(
                this.username,
                this.password,
                this.confirmPassword,
                reworkUkraineZoneId(this.zoneId),
                this.code
        );
    }
}
