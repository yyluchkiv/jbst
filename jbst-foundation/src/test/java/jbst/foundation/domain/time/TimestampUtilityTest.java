package jbst.foundation.domain.time;

import jbst.foundation.domain.constants.JbstConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.time.TimestampUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

class TimestampUtilityTest {
    private static Stream<Arguments> toUnixTimeTest() {
        return Stream.of(
                Arguments.of(1670526412123L, 1670526412L),
                Arguments.of(1670526412456L, 1670526412L),
                Arguments.of(1670526412789L, 1670526412L),
                Arguments.of(1670526412999L, 1670526412L),
                Arguments.of(1670526413001L, 1670526413L)
        );
    }

    private static Stream<Arguments> getStartOfMonthTimestampArgs() {
        return Stream.of(
                Arguments.of(1705474657000L, 1704060000000L),
                Arguments.of(1704059999000L, 1701381600000L)
        );
    }

    @RepeatedTest(100)
    void getCurrentTimestampTest() {
        // Arrange
        var expected = System.currentTimeMillis();

        // Act
        var actual = getCurrentTimestamp();

        // Assert
        assertThat(actual).isGreaterThanOrEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("toUnixTimeTest")
    void toUnixTimeTest(long timestamp, long expected) {
        // Act
        var actual = toUnixTime(timestamp);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @MethodSource("getStartOfMonthTimestampArgs")
    @ParameterizedTest
    void getStartOfMonthTimestampTest(long timestamp, long expected) {
        // Act
        var actual = getStartOfMonthTimestamp(timestamp, JbstConstants.ZoneIds.UKRAINE);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @RepeatedTest(100)
    void getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestampTest() {
        // Act
        var timestampUTC = getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestampUTC();
        var timestampUkraine = getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.UKRAINE);
        var timestampPoland = getCurrentMonthAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.POLAND);

        // Assert
        assertThat(timestampUTC).isGreaterThan(timestampPoland);
        assertThat(timestampPoland).isGreaterThan(timestampUkraine);
        assertThat(timestampPoland - timestampUkraine).isEqualTo(3600000L);
    }

    @RepeatedTest(100)
    void getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestampTest() {
        // Act
        var timestampUTC = getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestampUTC();
        var timestampUkraine = getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.UKRAINE);
        var timestampPoland = getPreviousMonthAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.POLAND);

        // Assert
        assertThat(timestampUTC).isGreaterThan(timestampPoland);
        assertThat(timestampPoland).isGreaterThan(timestampUkraine);
        assertThat(timestampPoland - timestampUkraine).isEqualTo(3600000L);
    }
}
