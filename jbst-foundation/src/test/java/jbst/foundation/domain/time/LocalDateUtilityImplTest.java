package jbst.foundation.domain.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.Month.*;
import static jbst.foundation.domain.random.JbstRandom.randomIntegerGreaterThanZeroByBounds;
import static jbst.foundation.domain.random.JbstRandom.randomZoneId;
import static jbst.foundation.domain.time.LocalDateUtility.*;
import static org.assertj.core.api.Assertions.assertThat;

class LocalDateUtilityImplTest {
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
