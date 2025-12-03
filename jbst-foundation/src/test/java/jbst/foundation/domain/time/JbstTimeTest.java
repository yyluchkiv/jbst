package jbst.foundation.domain.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static java.util.TimeZone.getTimeZone;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.time.JbstTime.convert;
import static jbst.foundation.domain.time.JbstTime.getAbsDifference;
import static org.assertj.core.api.Assertions.assertThat;

class JbstTimeTest {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    private static Stream<Arguments> convertArgs() {
        return Stream.of(
                Arguments.of(of(2021, DECEMBER, 25, 15, 16, 17), "25.12.2021 15:16:17"),
                Arguments.of(of(2011, DECEMBER, 25, 15, 16, 17), "25.12.2011 15:16:17"),
                Arguments.of(of(2001, DECEMBER, 25, 15, 16, 17), "25.12.2001 15:16:17")
        );
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    private static Stream<Arguments> getStartOfMonthArgs() {
        return Stream.of(
                Arguments.of(1705474657000L, 1704067200000L),
                Arguments.of(1704059999000L, 1701388800000L)
        );
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

    // =================================================================================================================
    // CONVERT(s)
    // =================================================================================================================
    @ParameterizedTest
    @MethodSource("convertArgs")
    void convertTest(LocalDateTime localDateTime, String expected) throws ParseException {
        // Arrange
        SDF.setTimeZone(getTimeZone(UKRAINE));

        // Act
        var actual = convert(localDateTime, UKRAINE);

        // Assert
        assertThat(actual).isEqualTo(SDF.parse(expected));
    }

    // =================================================================================================================
    // BLOCK: "timestamp"(s)
    // =================================================================================================================
    @MethodSource("getStartOfMonthArgs")
    @ParameterizedTest
    void getStartOfMonthTest(long timestamp, long expected) {
        // Act + Assert
        assertThat(JbstTime.getStartOfMonth(timestamp)).isEqualTo(expected);
    }

    // =================================================================================================================
    // BLOCK: java.util.Date
    // =================================================================================================================
    @ParameterizedTest
    @MethodSource("getAbsDifferenceArgs")
    void getAbsDifferenceTest(String date1, String date2, TimeUnit timeUnit, long expected) throws ParseException {
        // Act
        var actual = getAbsDifference(SDF.parse(date1), SDF.parse(date2), timeUnit);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
