package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.JbstUserTokenType;

import java.time.ZoneId;

import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record JbstRequestUserRegistration0(
        @Email.ValidEmail Email email,
        @Username.ValidUsername Username username,
        @Password.ValidPasswordCamelCaseLettersAndNumbers(min = 8, max = 20) Password password,
        @Password.ValidPasswordNotBlank Password confirmPassword,
        @Schema(type = "string") @NotNull ZoneId zoneId
) {

    public static JbstRequestUserRegistration0 fixed() {
        return new JbstRequestUserRegistration0(
                Email.fixed(),
                Username.of("registration01"),
                Password.fixed(),
                Password.fixed(),
                randomZoneId()
        );
    }

    public static JbstRequestUserRegistration0 random() {
        var password = Password.random();
        return new JbstRequestUserRegistration0(
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

    public JbstRequestUserRegistration0 createReworkedUkraineZoneId() {
        return new JbstRequestUserRegistration0(
                this.email,
                this.username,
                this.password,
                this.confirmPassword,
                reworkUkraineZoneId(this.zoneId)
        );
    }

    public JbstRequestUserToken asRequestUserToken() {
        return new JbstRequestUserToken(
                this.email,
                JbstUserTokenType.EMAIL_CONFIRMATION
        );
    }
}
