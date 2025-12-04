package jbst.foundation.domain.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static java.time.Month.DECEMBER;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ofPattern;
import static jbst.foundation.domain.time.LocalDateTimeUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

class LocalDateTimeUtilityImplTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime NOW_6_30 = LocalDate.now().atTime(6, 30); // to avoid failures on 59 min
    private static final LocalDateTime _25_11_2021 = LocalDateTime.of(2021, DECEMBER, 25, 15, 16, 17);

    private static Stream<Arguments> formatTest() {
        return Stream.of(
                Arguments.of(_25_11_2021, ISO_DATE_TIME, "2021-12-25T15:16:17"),
                Arguments.of(_25_11_2021, ofPattern("dd.MM.yyyy HH:mm:ss"), "25.12.2021 15:16:17")
        );
    }

    private static Stream<Arguments> parseTest() {
        return Stream.of(
                Arguments.of("2021-12-25T15:16:17", ISO_DATE_TIME, _25_11_2021),
                Arguments.of("25.12.2021 15:16:17", ofPattern("dd.MM.yyyy HH:mm:ss"), _25_11_2021)
        );
    }

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
    @MethodSource("formatTest")
    void formatTest(LocalDateTime localDateTime, DateTimeFormatter formatter, String expected) {
        // Act
        var actual = format(localDateTime, formatter);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("parseTest")
    void parseTest(String localDateTime, DateTimeFormatter formatter, LocalDateTime expected) {
        // Act
        var actual = parse(localDateTime, formatter);

        // Assert
        assertThat(actual).isEqualTo(expected);
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
}
