package jbst.foundation.domain.time;

import jbst.foundation.domain.constants.JbstConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.domain.time.JbstTime.convert;
import static jbst.foundation.domain.time.TimestampUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

class TimestampUtilityTest {
    private static final Long _2_HOUR_AGO = getPastTimestamp(Duration.ofHours(2L)).value();
    private static final Long _5_MINUTES_AGO = getPastTimestamp(Duration.ofMinutes(5)).value();
    private static final Long _1_MINUTE_AGO = getPastTimestamp(Duration.ofMinutes(1L)).value();
    private static final Long _2_MINUTES_FUTURE = getFutureTimestamp(Duration.ofMinutes(2L)).value();
    private static final Long _1_HOUR_FUTURE = getFutureTimestamp(Duration.ofHours(1L)).value();

    private static final long _5_SECONDS = new JbstTimeAmount(5L, SECONDS).toMillis();

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

    private static Stream<Arguments> isBetweenTest() {
        return Stream.of(
                Arguments.of(_5_MINUTES_AGO, _1_HOUR_FUTURE, true),
                Arguments.of(_5_MINUTES_AGO, _1_MINUTE_AGO, false),
                Arguments.of(_1_MINUTE_AGO, _5_MINUTES_AGO, false),
                Arguments.of(_1_HOUR_FUTURE, _5_MINUTES_AGO, false),
                Arguments.of(_1_HOUR_FUTURE, _2_MINUTES_FUTURE, false),
                Arguments.of(_2_MINUTES_FUTURE, _1_HOUR_FUTURE, false),
                Arguments.of(_1_MINUTE_AGO, _5_MINUTES_AGO, false),
                Arguments.of(_1_MINUTE_AGO, _2_HOUR_AGO, false),
                Arguments.of(_1_MINUTE_AGO, _2_MINUTES_FUTURE, true),
                Arguments.of(_1_MINUTE_AGO, _1_HOUR_FUTURE, true)
        );
    }

    private static Stream<Arguments> isBetweenInclusiveTest() {
        return Stream.of(
                Arguments.of(1, 0, 2, true),
                Arguments.of(1, 1, 2, true),
                Arguments.of(1, 2, 2, false),
                Arguments.of(1, 0, 1, true),
                Arguments.of(1, 0, 0, false),
                Arguments.of(1, 1, 1, true),
                Arguments.of(1, -3, -2, false),
                Arguments.of(1, 2, 3, false)
        );
    }

    private static Stream<Arguments> isPastTest() {
        return Stream.of(
                Arguments.of(1642767625000L, true),
                Arguments.of(1642767626000L, true),
                Arguments.of(getCurrentTimestamp() + 10000L, false)
        );
    }

    private static Stream<Arguments> isFutureTest() {
        return Stream.of(
                Arguments.of(1642767625000L, false),
                Arguments.of(1642767626000L, false),
                Arguments.of(getCurrentTimestamp() + 10000L, true)
        );
    }

    private static Stream<Arguments> isCurrentTimestampNSecondsMoreTest() {
        return Stream.of(
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 1L, true),
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 2L, true),
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 3L, true),
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 7L, false),
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 8L, false),
                Arguments.of(getCurrentTimestamp() - _5_SECONDS, 9L, false)
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

    @RepeatedTest(10)
    void getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestampTest() {
        // Act
        var timestampUTC = getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestampUTC(4);
        var timestampUkraine = getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.UKRAINE, 3);
        var timestampPoland = getNMonthAgoAtStartOfMonthAndAtStartOfDayTimestamp(JbstConstants.ZoneIds.POLAND, 3);

        // Assert
        assertThat(timestampUTC)
                .isLessThan(timestampPoland)
                .isLessThan(timestampUkraine);
        assertThat(timestampPoland).isGreaterThan(timestampUkraine);
        var localDateTimeUTC = convert(timestampUTC, UTC);
        var localDateTimeUkraine = convert(timestampUkraine, JbstConstants.ZoneIds.UKRAINE);
        var localDateTimePoland = convert(timestampPoland, JbstConstants.ZoneIds.POLAND);
        assertThat(localDateTimeUTC.toString()).endsWith("00:00");
        assertThat(localDateTimeUkraine.toString()).endsWith("00:00");
        assertThat(localDateTimePoland.toString()).endsWith("00:00");
    }

    @RepeatedTest(10)
    void getPastRangeTest() {
        // Arrange
        var currentTimestamp = getCurrentTimestamp();

        // Act
        var actual = getPastRange(currentTimestamp, new JbstTimeAmount(5, SECONDS));

        // Assert
        assertThat(actual.to()).isGreaterThan(actual.from());
        assertThat(actual.to()).isGreaterThanOrEqualTo(currentTimestamp - 5000);
    }

    @RepeatedTest(10)
    void getFutureRangeTest() {
        // Arrange
        var currentTimestamp = getCurrentTimestamp();

        // Act
        var actual = getFutureRange(currentTimestamp, new JbstTimeAmount(5, SECONDS));

        // Assert
        assertThat(actual.to()).isGreaterThan(actual.from());
        assertThat(actual.to()).isGreaterThanOrEqualTo(currentTimestamp + 5000);
    }

    @ParameterizedTest
    @MethodSource("isBetweenTest")
    void isBetweenTest(long past, long future, boolean expected) {
        // Act
        var actual = isBetween(getCurrentTimestamp(), past, future);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isBetweenInclusiveTest")
    void isBetweenInclusiveTest(long timestamp, long past, long future, boolean expected) {
        // Act
        var actual = isBetweenInclusive(timestamp, past, future);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isPastTest")
    void isPastTest(long timestamp, boolean expected) {
        // Act
        var actual = isPast(timestamp);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFutureTest")
    void isFutureTest(long timestamp, boolean expected) {
        // Act
        var actual = isFuture(timestamp);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isCurrentTimestampNSecondsMoreTest")
    void isCurrentTimestampNSecondsMoreTest(long timestamp, long seconds, boolean expected) {
        // Act
        var actual = isCurrentTimestampNSecondsMore(timestamp, seconds);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
