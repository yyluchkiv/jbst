package jbst.foundation.domain.zones;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;

@UtilityClass
public class JbstZones {

    public static ZoneId reworkUkraineZoneId(@NotNull ZoneId zoneId) {
        if ("Europe/Kiev".equals(zoneId.getId())) {
            return JbstConstants.ZoneIds.UKRAINE;
        }
        return zoneId;
    }
}
