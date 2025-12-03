package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record RequestUserUpdate2(
        @Schema(type = "string") @NotNull ZoneId zoneId,
        String name
) {

    public static RequestUserUpdate2 hardcoded() {
        return new RequestUserUpdate2(
                UKRAINE,
                "jbst"
        );
    }

    public RequestUserUpdate2 createReworkedUkraineZoneId() {
        return new RequestUserUpdate2(
                reworkUkraineZoneId(this.zoneId),
                this.name
        );
    }
}
