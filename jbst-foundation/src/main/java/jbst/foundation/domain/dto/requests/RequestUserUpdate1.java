package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;

import java.time.ZoneId;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.utilities.zones.JbstZones.reworkUkraineZoneId;

public record RequestUserUpdate1(
        @Schema(type = "string") @NotNull ZoneId zoneId,
        @Email.ValidEmail Email email,
        String name
) {

    public static RequestUserUpdate1 hardcoded() {
        return new RequestUserUpdate1(
                UKRAINE,
                Email.hardcoded(),
                "jbst"
        );
    }

    public RequestUserUpdate1 createReworkedUkraineZoneId() {
        return new RequestUserUpdate1(
                reworkUkraineZoneId(this.zoneId),
                this.email,
                this.name
        );
    }
}
