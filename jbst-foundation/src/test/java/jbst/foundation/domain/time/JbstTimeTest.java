package jbst.foundation.domain.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.time.JbstTime.*;
import static org.assertj.core.api.Assertions.assertThat;

class JbstTimeTest {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
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
    void convertTest1(LocalDateTime localDateTime, String expected) throws ParseException {
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
    void convertTest2(Date date, ZoneId zoneId, LocalDateTime expected) {
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
    void convertTest3(Long timestamp, ZoneId zoneId, LocalDateTime expected) {
        // Act + Assert
        assertThat(convert(timestamp, zoneId)).isEqualTo(expected);
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
