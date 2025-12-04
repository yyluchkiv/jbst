package jbst.foundation.domain.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Stream;

import static java.time.Month.*;
import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.randomIntegerGreaterThanZeroByBounds;
import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.time.LocalDateUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

class LocalDateUtilityImplTest {
    private static final LocalDate _25_11_2021 = LocalDate.of(2021, DECEMBER, 25);

    private static Stream<Arguments> convertDateTest() {
        return Stream.of(
                Arguments.of(new Date(1640438177000L), _25_11_2021),
                Arguments.of(new Date(1640445377000L), _25_11_2021),
                Arguments.of(new Date(1324818977000L), _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1324826177000L), _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1009286177000L), _25_11_2021.minusYears(20)),
                Arguments.of(new Date(1009293377000L), _25_11_2021.minusYears(20)),
                Arguments.of(new Date(1645999200000L), new java.sql.Date(new Date(1645999200000L).getTime()).toLocalDate()), // 28.02.2022
                Arguments.of(new Date(1653944400000L), new java.sql.Date(new Date(1653944400000L).getTime()).toLocalDate()) // 31.05.2022
        );
    }

    private static Stream<Arguments> convertDateZoneIdTest() {
        return Stream.of(
                Arguments.of(new Date(1640438177000L), UKRAINE, _25_11_2021),
                Arguments.of(new Date(1640445377000L), UTC, _25_11_2021),
                Arguments.of(new Date(1324818977000L), UKRAINE, _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1324826177000L), UTC, _25_11_2021.minusYears(10)),
                Arguments.of(new Date(1009286177000L), UKRAINE, _25_11_2021.minusYears(20)),
                Arguments.of(new Date(1009293377000L), UTC, _25_11_2021.minusYears(20))
        );
    }

    private static Stream<Arguments> isFirstDayOfMonthTest() {
        return Stream.of(
                Arguments.of(LocalDate.of(2021, DECEMBER, 28), false),
                Arguments.of(LocalDate.of(2021, FEBRUARY, 28), false),
                Arguments.of(LocalDate.of(2021, AUGUST, 28), false),
                Arguments.of(LocalDate.of(2021, DECEMBER, 1), true),
                Arguments.of(LocalDate.of(2021, MARCH, 1), true),
                Arguments.of(LocalDate.of(2021, JUNE, 1), true)
        );
    }

    private static Stream<Arguments> isLastDayOfMonthTest() {
        return Stream.of(
                Arguments.of(LocalDate.of(2021, DECEMBER, 28), false),
                Arguments.of(LocalDate.of(2021, FEBRUARY, 28), true),
                Arguments.of(LocalDate.of(2021, AUGUST, 28), false),
                Arguments.of(LocalDate.of(2021, DECEMBER, 1), false),
                Arguments.of(LocalDate.of(2021, MARCH, 1), false),
                Arguments.of(LocalDate.of(2021, JUNE, 1), false),
                Arguments.of(LocalDate.of(2021, AUGUST, 30), false),
                Arguments.of(LocalDate.of(2021, AUGUST, 31), true),
                Arguments.of(LocalDate.of(2021, JANUARY, 30), false),
                Arguments.of(LocalDate.of(2021, JANUARY, 31), true)
        );
    }

    @ParameterizedTest
    @MethodSource("convertDateTest")
    void convertDateTest(Date date, LocalDate expected) {
        // Act
        var actual = convertDate(date);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("convertDateZoneIdTest")
    void convertDateZoneIdTest(Date date, ZoneId zoneId, LocalDate expected) {
        // Act
        var actual = convertDate(date, zoneId);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getFirstDayCurrentMonthTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getFirstDayCurrentMonth(zoneId);

        // Assert
        assertThat(actual.getDayOfMonth()).isEqualTo(1);
        var now = LocalDate.now(zoneId);
        assertThat(actual.getMonth()).isEqualTo(now.getMonth());
        assertThat(actual.getYear()).isEqualTo(now.getYear());
    }

    @Test
    void getFirstDayPreviousMonthTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getFirstDayPreviousMonth(zoneId);

        // Assert
        assertThat(actual.getDayOfMonth()).isEqualTo(1);
        var now = LocalDate.now(zoneId);
        assertThat(actual.getMonth()).isEqualTo(now.getMonth().minus(1));
    }

    @Test
    void getFirstDayTwoMonthAgoTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getFirstDayTwoMonthAgo(zoneId);

        // Assert
        assertThat(actual.getDayOfMonth()).isEqualTo(1);
        var now = LocalDate.now(zoneId);
        assertThat(actual.getMonth()).isEqualTo(now.getMonth().minus(2));
    }

    @Test
    void getFirstDayMonthsAgoTest() {
        // Arrange
        int months = randomIntegerGreaterThanZeroByBounds(3, 5);
        var zoneId = randomZoneId();

        // Act
        var actual = getFirstDayMonthsAgo(zoneId, months);

        // Assert
        assertThat(actual.getDayOfMonth()).isEqualTo(1);
        var now = LocalDate.now(zoneId);
        assertThat(actual.getMonth()).isEqualTo(now.getMonth().minus(months));
    }

    @Test
    void getLastDayCurrentMonthTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getLastDayCurrentMonth(zoneId);

        // Assert
        var now = LocalDate.now(zoneId);
        assertThat(actual.getDayOfMonth()).isGreaterThanOrEqualTo(now.getDayOfMonth());
        assertThat(actual.getMonth()).isEqualTo(now.getMonth());
        assertThat(actual.getYear()).isEqualTo(now.getYear());
    }

    @Test
    void getLastDayPreviousMonthTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getLastDayPreviousMonth(zoneId);

        // Assert
        var previousMonth = LocalDate.now(zoneId).minusMonths(1);
        assertThat(actual.getDayOfMonth()).isGreaterThanOrEqualTo(previousMonth.getDayOfMonth());
        assertThat(actual.getMonth()).isEqualTo(previousMonth.getMonth());
    }

    @Test
    void getLastDayTwoMonthAgoTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getLastDayTwoMonthAgo(zoneId);

        // Assert
        var twoMonthAgo = LocalDate.now(zoneId).minusMonths(2);
        assertThat(actual.getDayOfMonth()).isGreaterThanOrEqualTo(twoMonthAgo.getDayOfMonth());
        assertThat(actual.getMonth()).isEqualTo(twoMonthAgo.getMonth());
    }

    @Test
    void getLastDayMonthsAgoTest() {
        // Arrange
        int months = randomIntegerGreaterThanZeroByBounds(3, 5);
        var zoneId = randomZoneId();

        // Act
        var actual = getLastDayMonthsAgo(zoneId, months);

        // Assert
        var twoMonthAgo = LocalDate.now(zoneId).minusMonths(months);
        assertThat(actual.getDayOfMonth()).isGreaterThanOrEqualTo(twoMonthAgo.getDayOfMonth());
        assertThat(actual.getMonth()).isEqualTo(twoMonthAgo.getMonth());
    }

    @ParameterizedTest
    @MethodSource("isFirstDayOfMonthTest")
    void isFirstDayOfMonthTest(LocalDate localDate, boolean expected) {
        // Act
        var actual = isFirstDayOfMonth(localDate);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isLastDayOfMonthTest")
    void isLastDayOfMonthTest(LocalDate localDate, boolean expected) {
        // Act
        var actual = isLastDayOfMonth(localDate);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCurrentDayOfMonthTest() {
        // Arrange
        var zoneId = randomZoneId();

        // Act
        var actual = getCurrentDayOfMonth(zoneId);

        // Assert
        assertThat(actual).isEqualTo(LocalDate.now(zoneId).getDayOfMonth());
    }
}
