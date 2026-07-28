package jbst.foundation.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.zones.JbstZones.reworkUkraineZoneId;

public record JbstRequestMagicLinkToken(
        @Schema(type = "string")
        @NotBlank String value,
        @Schema(type = "string") @NotNull ZoneId zoneId
) {

    public static JbstRequestMagicLinkToken fixed() {
        return new JbstRequestMagicLinkToken("E4944FFE506B2838A8F667D95C5FB28DB3ABAE54", UKRAINE);
    }

    public static JbstRequestMagicLinkToken random() {
        return new JbstRequestMagicLinkToken(randomString(), randomZoneId());
    }

    public JbstRequestMagicLinkToken createReworkedUkraineZoneId() {
        return new JbstRequestMagicLinkToken(
                this.value,
                reworkUkraineZoneId(this.zoneId)
        );
    }
}
