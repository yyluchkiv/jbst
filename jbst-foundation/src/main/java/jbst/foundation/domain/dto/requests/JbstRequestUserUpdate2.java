package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record JbstRequestUserUpdate2(
        @Schema(type = "string") @NotNull ZoneId zoneId,
        String name
) {

    public static JbstRequestUserUpdate2 fixed() {
        return new JbstRequestUserUpdate2(
                UKRAINE,
                "jbst"
        );
    }

    public JbstRequestUserUpdate2 createReworkedUkraineZoneId() {
        return new JbstRequestUserUpdate2(
                reworkUkraineZoneId(this.zoneId),
                this.name
        );
    }
}
