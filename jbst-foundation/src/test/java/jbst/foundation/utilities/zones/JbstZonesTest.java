package jbst.foundation.utilities.zones;

import jbst.foundation.domain.constants.JbstConstants;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneId;
import java.util.stream.Stream;

import static jbst.foundation.utilities.zones.JbstZones.reworkUkraineZoneId;
import static org.assertj.core.api.Assertions.assertThat;

class JbstZonesTest {

    private static Stream<Arguments> reworkUkraineZoneIdArgs() {
        return Stream.of(
                Arguments.of(ZoneId.of("Europe/Kiev"), JbstConstants.ZoneIds.UKRAINE),
                Arguments.of(ZoneId.of("Europe/Kiev"), JbstConstants.ZoneIds.UKRAINE),
                Arguments.of(ZoneId.of("Poland"), JbstConstants.ZoneIds.POLAND)
        );
    }

    @ParameterizedTest
    @MethodSource("reworkUkraineZoneIdArgs")
    void reworkUkraineZoneIdTest(ZoneId zoneId, ZoneId expected) {
        // Act + Assert
        assertThat(reworkUkraineZoneId(zoneId)).isEqualTo(expected);
    }
}
