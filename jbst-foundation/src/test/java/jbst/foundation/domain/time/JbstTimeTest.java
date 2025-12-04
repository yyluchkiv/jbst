package jbst.foundation.domain.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.TimeZone.getTimeZone;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.time.JbstTime.*;
import static jbst.foundation.domain.time.TimestampUtility.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;

class JbstTimeTest {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime NOW_6_30 = LocalDate.now().atTime(6, 30); // to avoid failures on 59 min
    private static final LocalDateTime _25_11_2021 = LocalDateTime.of(2021, DECEMBER, 25, 15, 16, 17);

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    private static Stream<Arguments> convertArgs1() {
        return Stream.of(
                Arguments.of(of(2021, DECEMBER, 25, 15, 16, 17), "25.12.2021 15:16:17"),
                Arguments.of(of(2011, DECEMBER, 25, 15, 16, 17), "25.12.2011 15:16:17"),
                Arguments.of(of(2001, DECEMBER, 25, 15, 16, 17), "25.12.2001 15:16:17")
        );
    }
    @ParameterizedTest
    @MethodSource("convertArgs1")
    void convertTest(LocalDateTime localDateTime, String expected) throws ParseException {
        // Arrange
        SDF.setTimeZone(getTimeZone(UKRAINE));

        // Act + Assert
        assertThat(convert(localDateTime, UKRAINE)).isEqualTo(SDF.parse(expected));
    }

    private static Stream<Arguments> convertArgs2() {
        return Stream.of(
                Arguments.of(new Date(1640438177000L), UKRAINE, _25_11_2021),
                Arguments.of(new Date(1640445377000L), UTC, _25_11_2021),
                Arguments.of(new Date(1324818977000L), UKRAINE, _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1324826177000L), UTC, _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1009286177000L), UKRAINE, _25_11_2021.minusYears(20)),
                Arguments.of(new Date(1009293377000L), UTC, _25_11_2021.minusYears(20))
        );
    }
    @ParameterizedTest
    @MethodSource("convertArgs2")
    void convertTest(Date date, ZoneId zoneId, LocalDateTime expected) {
        // Act + Assert
        assertThat(convert(date, zoneId)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertArgs3() {
        return Stream.of(
                Arguments.of(1640438177000L, UKRAINE, _25_11_2021),
                Arguments.of(1640445377000L, UTC, _25_11_2021),
                Arguments.of(1324818977000L, UKRAINE, _25_11_2021.minusYears(10)),
                Arguments.of(1324826177000L, UTC, _25_11_2021.minusYears(10)),
                Arguments.of(1009286177000L, UKRAINE, _25_11_2021.minusYears(20)),
                Arguments.of(1009293377000L, UTC, _25_11_2021.minusYears(20))
        );
    }
    @ParameterizedTest
    @MethodSource("convertArgs3")
    void convertTest(Long timestamp, ZoneId zoneId, LocalDateTime expected) {
        // Act + Assert
        assertThat(convert(timestamp, zoneId)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertArgs4() {
        return Stream.of(
                Arguments.of(new Date(1640438177000L), _25_11_2021.toLocalDate()),
                Arguments.of(new Date(1640445377000L), _25_11_2021.toLocalDate()),
                Arguments.of(new Date(1324818977000L), _25_11_2021.toLocalDate().minusYears(10)),
                Arguments.of(new Date(1324826177000L), _25_11_2021.toLocalDate().minusYears(10)),
                Arguments.of(new Date(1009286177000L), _25_11_2021.toLocalDate().minusYears(20)),
                Arguments.of(new Date(1009293377000L), _25_11_2021.toLocalDate().minusYears(20)),
                Arguments.of(new Date(1645999200000L), new java.sql.Date(new Date(1645999200000L).getTime()).toLocalDate()), // 28.02.2022
                Arguments.of(new Date(1653944400000L), new java.sql.Date(new Date(1653944400000L).getTime()).toLocalDate()) // 31.05.2022
        );
    }
    @ParameterizedTest
    @MethodSource("convertArgs4")
    void convertTest(Date date, LocalDate expected) {
        // Assert
        assertThat(convert4(date)).isEqualTo(expected);
    }

    private static Stream<Arguments> convertArgs5() {
        return Stream.of(
                Arguments.of(new Date(1640438177000L), UKRAINE, _25_11_2021.toLocalDate()),
                Arguments.of(new Date(1640445377000L), UTC, _25_11_2021.toLocalDate()),
                Arguments.of(new Date(1324818977000L), UKRAINE, _25_11_2021.toLocalDate().minusYears(10)),
                Arguments.of(new Date(1324826177000L), UTC, _25_11_2021.toLocalDate().minusYears(10)),
                Arguments.of(new Date(1009286177000L), UKRAINE, _25_11_2021.toLocalDate().minusYears(20)),
                Arguments.of(new Date(1009293377000L), UTC, _25_11_2021.toLocalDate().minusYears(20))
        );
    }
    @ParameterizedTest
    @MethodSource("convertArgs5")
    void convertTest(Date date, ZoneId zoneId, LocalDate expected) {
        // Act + Assert
        assertThat(convert5(date, zoneId)).isEqualTo(expected);
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    private static Stream<Arguments> getTimestampArgs() {
        return Stream.of(
                Arguments.of(_25_11_2021, UKRAINE, 1640438177000L),
                Arguments.of(_25_11_2021, UTC, 1640445377000L),
                Arguments.of(_25_11_2021.minusYears(10), UKRAINE, 1324818977000L),
                Arguments.of(_25_11_2021.minusYears(10), UTC, 1324826177000L),
                Arguments.of(_25_11_2021.minusYears(20), UKRAINE, 1009286177000L),
                Arguments.of(_25_11_2021.minusYears(20), UTC, 1009293377000L)
        );
    }
    @ParameterizedTest
    @MethodSource("getTimestampArgs")
    void getTimestampTest(LocalDateTime localDateTime, ZoneId zoneId, long expected) {
        // Act + Assert
        assertThat(getTimestamp(localDateTime, zoneId)).isEqualTo(expected);
    }

    private static Stream<Arguments> getStartOfMonthArgs() {
        return Stream.of(
                Arguments.of(1705474657000L, 1704067200000L),
                Arguments.of(1704059999000L, 1701388800000L)
        );
    }
    @MethodSource("getStartOfMonthArgs")
    @ParameterizedTest
    void getStartOfMonthTest(long timestamp, long expected) {
        // Act + Assert
        assertThat(JbstTime.getStartOfMonth(timestamp)).isEqualTo(expected);
    }

    private static Stream<Arguments> isCurrentTimestampNSecondsMoreArgs() {
        return Stream.of(
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 1L, true),
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 2L, true),
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 3L, true),
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 7L, false),
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 8L, false),
                Arguments.of(getCurrentTimestamp() - new JbstTimeAmount(5L, SECONDS).toMillis(), 9L, false)
        );
    }
    @ParameterizedTest
    @MethodSource("isCurrentTimestampNSecondsMoreArgs")
    void isCurrentTimestampNSecondsMoreTest(long timestamp, long seconds, boolean expected) {
        // Act + Assert
        assertThat(isCurrentTimestampNSecondsMore(timestamp, seconds)).isEqualTo(expected);
    }

    // =================================================================================================================
    // BLOCK: LocalDateTime
    // =================================================================================================================
    private static Stream<Arguments> isParamsEqualsTruncatedBySecondsTest() {
        return Stream.of(
                Arguments.of(NOW.plusSeconds(2), NOW.plusSeconds(3), false),
                Arguments.of(NOW.plusSeconds(2), NOW.plusSeconds(2), true)
        );
    }

    private static Stream<Arguments> isParamsEqualsTruncatedByTest() {
        return Stream.of(
                Arguments.of(NOW.plusHours(2), NOW.plusHours(3), false),
                Arguments.of(NOW.plusHours(2), NOW.plusHours(2), true)
        );
    }

    private static Stream<Arguments> isFirstParamAfterTruncatedBySecondsTest() {
        return Stream.of(
                Arguments.of(NOW, NOW.plusSeconds(3), false),
                Arguments.of(NOW.plusSeconds(2), NOW.plusSeconds(2), false),
                Arguments.of(NOW.plusSeconds(2), NOW, true)
        );
    }

    private static Stream<Arguments> isFirstParamAfterTruncatedByTest() {
        return Stream.of(
                Arguments.of(NOW.plusMinutes(2), NOW.plusMinutes(3), false),
                Arguments.of(NOW, NOW.plusHours(3), false),
                Arguments.of(NOW.plusHours(2), NOW.plusHours(2), false),
                Arguments.of(NOW.plusHours(2), NOW, true)
        );
    }

    private static Stream<Arguments> isFirstParamAfterOrEqualTruncatedBySecondsTest() {
        return Stream.of(
                Arguments.of(NOW, NOW.plusSeconds(3), false),
                Arguments.of(NOW.plusMinutes(2), NOW.plusMinutes(2), true),
                Arguments.of(NOW.plusMinutes(2), NOW, true)
        );
    }

    private static Stream<Arguments> isFirstParamAfterOrEqualTruncatedByTest() {
        return Stream.of(
                Arguments.of(NOW_6_30.plusMinutes(2), NOW_6_30.plusMinutes(3), true),
                Arguments.of(NOW_6_30, NOW_6_30.plusHours(3), false),
                Arguments.of(NOW_6_30.plusDays(2), NOW_6_30.plusDays(2), true),
                Arguments.of(NOW_6_30.plusDays(2), NOW_6_30, true)
        );
    }

    private static Stream<Arguments> isFirstParamBeforeTruncatedBySecondsTest() {
        return Stream.of(
                Arguments.of(NOW.plusSeconds(3), NOW, false),
                Arguments.of(NOW.plusMinutes(2), NOW.plusMinutes(2), false),
                Arguments.of(NOW, NOW.plusMinutes(2), true)
        );
    }

    private static Stream<Arguments> isFirstParamBeforeTruncatedByTest() {
        return Stream.of(
                Arguments.of(NOW_6_30.plusMinutes(2), NOW_6_30.plusMinutes(3), false),
                Arguments.of(NOW_6_30.plusDays(3), NOW_6_30, false),
                Arguments.of(NOW_6_30.plusDays(2), NOW_6_30.plusDays(2), false),
                Arguments.of(NOW_6_30, NOW_6_30.plusDays(2), true)
        );
    }

    private static Stream<Arguments> isFirstParamBeforeOrEqualTruncatedBySecondsTest() {
        return Stream.of(
                Arguments.of(NOW.plusSeconds(3), NOW, false),
                Arguments.of(NOW.plusMinutes(2), NOW.plusMinutes(2), true),
                Arguments.of(NOW, NOW.plusMinutes(2), true)
        );
    }

    private static Stream<Arguments> isFirstParamBeforeOrEqualTruncatedByTest() {
        return Stream.of(
                Arguments.of(NOW.plusMinutes(2), NOW.plusMinutes(3), true),
                Arguments.of(NOW.plusDays(3), NOW, false),
                Arguments.of(NOW.plusDays(2), NOW.plusDays(2), true),
                Arguments.of(NOW, NOW.plusDays(2), true)
        );
    }

    @ParameterizedTest
    @MethodSource("isParamsEqualsTruncatedBySecondsTest")
    void isParamsEqualsTruncatedBySecondsTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isParamsEqualsTruncatedBySeconds(time1, time2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isParamsEqualsTruncatedByTest")
    void isParamsEqualsTruncatedByTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isParamsEqualsTruncatedBy(time1, time2, ChronoUnit.HOURS);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamAfterTruncatedBySecondsTest")
    void isFirstParamAfterTruncatedBySecondsTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamAfterTruncatedBySeconds(time1, time2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamAfterTruncatedByTest")
    void isFirstParamAfterTruncatedByTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamAfterTruncatedBy(time1, time2, ChronoUnit.HOURS);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamAfterOrEqualTruncatedBySecondsTest")
    void isFirstParamAfterOrEqualTruncatedBySecondsTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamAfterOrEqualTruncatedBySeconds(time1, time2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamAfterOrEqualTruncatedByTest")
    void isFirstParamAfterOrEqualTruncatedByTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamAfterOrEqualTruncatedBy(time1, time2, ChronoUnit.HOURS);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamBeforeTruncatedBySecondsTest")
    void isFirstParamBeforeTruncatedBySecondsTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamBeforeTruncatedBySeconds(time1, time2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamBeforeTruncatedByTest")
    void isFirstParamBeforeTruncatedByTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamBeforeTruncatedBy(time1, time2, ChronoUnit.HOURS);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamBeforeOrEqualTruncatedBySecondsTest")
    void isFirstParamBeforeOrEqualTruncatedBySecondsTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamBeforeOrEqualTruncatedBySeconds(time1, time2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstParamBeforeOrEqualTruncatedByTest")
    void isFirstParamBeforeOrEqualTruncatedByTest(LocalDateTime time1, LocalDateTime time2, boolean expected) {
        // Act
        var actual = isFirstParamBeforeOrEqualTruncatedBy(time1, time2, ChronoUnit.HOURS);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    // =================================================================================================================
    // BLOCK: java.util.Date
    // =================================================================================================================
    private static Stream<Arguments> getAbsDifferenceArgs() {
        return Stream.of(
                Arguments.of("25.12.2021 15:16:17", "25.12.2011 15:16:17", TimeUnit.DAYS, 3653L),
                Arguments.of("25.12.2021 15:16:17", "25.12.2011 15:16:17", TimeUnit.MINUTES, 5260320L),
                Arguments.of("25.12.2021 15:16:17", "25.12.2021 16:00:00", TimeUnit.DAYS, 0L),
                Arguments.of("25.12.2021 15:16:17", "25.12.2021 16:00:00", TimeUnit.HOURS, 0L),
                Arguments.of("25.12.2021 15:16:17", "25.12.2021 16:00:00", TimeUnit.MINUTES, 43L),
                Arguments.of("25.12.2021 15:16:17", "25.12.2021 16:00:00", TimeUnit.SECONDS, 2623L)
        );
    }
    @ParameterizedTest
    @MethodSource("getAbsDifferenceArgs")
    void getAbsDifferenceTest(String date1, String date2, TimeUnit timeUnit, long expected) throws ParseException {
        // Act
        var actual = getAbsDifference(SDF.parse(date1), SDF.parse(date2), timeUnit);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
