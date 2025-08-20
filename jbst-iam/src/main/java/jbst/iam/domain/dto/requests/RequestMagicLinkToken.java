package jbst.iam.domain.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZoneOffset;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.randomZoneId;
import static jbst.foundation.utilities.zones.ZonesUtility.reworkUkraineZoneId;

public record RequestMagicLinkToken(
        @Schema(type = "string")
        @NotBlank String value,
        @Schema(type = "string") @NotNull ZoneId zoneId
) {

    public static RequestMagicLinkToken hardcoded() {
        return new RequestMagicLinkToken("E4944FFE506B2838A8F667D95C5FB28DB3ABAE54", ZoneOffset.UTC);
    }

    public static RequestMagicLinkToken random() {
        return new RequestMagicLinkToken(randomString(), randomZoneId());
    }

    public RequestMagicLinkToken createReworkedUkraineZoneId() {
        return new RequestMagicLinkToken(
                this.value,
                reworkUkraineZoneId(this.zoneId)
        );
    }
}
