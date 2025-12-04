package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;

import java.time.ZoneId;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record JbstRequestUserRegistration1(
        @Username.ValidUsername Username username,
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password password,
        @Password.ValidPasswordNotBlank Password confirmPassword,
        @Schema(type = "string") @NotNull ZoneId zoneId,
        @NotBlank String code
) {

    public static JbstRequestUserRegistration1 hardcoded() {
        return new JbstRequestUserRegistration1(
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

    public JbstRequestUserRegistration1 createReworkedUkraineZoneId() {
        return new JbstRequestUserRegistration1(
                this.username,
                this.password,
                this.confirmPassword,
                reworkUkraineZoneId(this.zoneId),
                this.code
        );
    }
}
