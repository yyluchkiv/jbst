package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.base.Email;

import java.time.ZoneId;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record JbstRequestUserUpdate1(
        @Schema(type = "string") @NotNull ZoneId zoneId,
        @Email.ValidEmail Email email,
        String name
) {

    public static JbstRequestUserUpdate1 hardcoded() {
        return new JbstRequestUserUpdate1(
                UKRAINE,
                Email.hardcoded(),
                "jbst"
        );
    }

    public JbstRequestUserUpdate1 createReworkedUkraineZoneId() {
        return new JbstRequestUserUpdate1(
                reworkUkraineZoneId(this.zoneId),
                this.email,
                this.name
        );
    }
}
