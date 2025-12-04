package jbst.foundation.domain.asserts;

import jbst.foundation.domain.base.PropertyId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.TWO;
import static jbst.foundation.domain.constants.JbstConstants.ZoneIds.UKRAINE;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class JbstAssertsTests {

    // =================================================================================================================
    // 1 assert complexity
    // =================================================================================================================
    private static Stream<Arguments> assertNonNullOrThrowTest() {
        return Stream.of(
                Arguments.of(new Object(), null),
                Arguments.of(null, randomString())
        );
    }

    private static Stream<Arguments> assertNonBlankOrThrowTest() {
        return Stream.of(
                Arguments.of(randomString(), null),
                Arguments.of("", randomString()),
                Arguments.of(" ", randomString())
        );
    }

    private static Stream<Arguments> assertNonEmptyOrThrowTest() {
        return Stream.of(
                Arguments.of(List.of(randomString(), randomString()), null),
                Arguments.of(emptyList(), randomString()),
                Arguments.of(emptySet(), randomString())
        );
    }

    private static Stream<Arguments> assertTrueOrThrowTest() {
        return Stream.of(
                Arguments.of(true, null),
                Arguments.of(false, randomString()),
                Arguments.of(false, randomString())
        );
    }

    private static Stream<Arguments> assertFalseOrThrowTest() {
        return Stream.of(
                Arguments.of(false, null),
                Arguments.of(true, randomString()),
                Arguments.of(true, randomString())
        );
    }

    private static Stream<Arguments> requireNonNullOrThrowTest() {
        return Stream.of(
                Arguments.of(randomString(), null),
                Arguments.of(null, randomString())
        );
    }

    private static Stream<Arguments> assertUniqueOrThrowTest() {
        return Stream.of(
                Arguments.of(randomString(), null),
                Arguments.of(null, null),
                Arguments.of("ONE", "Property field-name must be unique"),
                Arguments.of("THREE", "Property field-name must be unique")
        );
    }

    private static Stream<Arguments> assertLongsArgs() {
        return Stream.of(
                Arguments.of(-100, false, false, true, true),
                Arguments.of(-1, false, false, true, true),
                Arguments.of(0, false, true, false, true),
                Arguments.of(1, true, true, false, false),
                Arguments.of(100, true, true, false, false)
        );
    }

    private static Stream<Arguments> assertBigDecimalsArgs() {
        return Stream.of(
                Arguments.of(new BigDecimal("-100.00"), false, false, true, true),
                Arguments.of(new BigDecimal("-1.00"), false, false, true, true),
                Arguments.of(ZERO, false, true, false, true),
                Arguments.of(new BigDecimal("0.00"), false, true, false, true),
                Arguments.of(new BigDecimal("1.00"), true, true, false, false),
                Arguments.of(new BigDecimal("100.00"), true, true, false, false)
        );
    }

    private static Stream<Arguments> assertBigDecimalsBetweenArgs() {
        return Stream.of(
                Arguments.of(ONE.negate(), ZERO, TWO, true, true),
                Arguments.of(ONE.negate(), ONE.negate(), TWO, false, true),
                Arguments.of(ONE.negate(), TWO, TWO, false, true),
                Arguments.of(ONE.negate(), HUNDRED, TWO, false, false)
        );
    }

    private static Stream<Arguments> assertSortedOrThrowTest() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 3, 5, 6), Comparator.naturalOrder(), true),
                Arguments.of(List.of(1, 2, 2, 3, 3, 5, 5, 6), Comparator.naturalOrder(), true),
                Arguments.of(List.of(1, 2, 3, 5, 6), Comparator.naturalOrder().reversed(), false),
                Arguments.of(List.of(6, 5, 4, 2, 1, 100), Comparator.naturalOrder().reversed(), false),
                Arguments.of(List.of(100, 1, 0, -1, -100), Comparator.naturalOrder().reversed(), true),
                Arguments.of(List.of(100, 1, 0, 0, 0, 0, -1, -100), Comparator.naturalOrder().reversed(), true)
        );
    }

    // =================================================================================================================
    // 1+ asserts complexity
    // =================================================================================================================
    private static Stream<Arguments> assertZoneIdOrThrowTest() {
        return Stream.of(
                Arguments.of("", randomString()),
                Arguments.of("Europe/Kiev1", randomString()),
                Arguments.of("Europe/Kiev2", randomString()),
                Arguments.of(UKRAINE.getId(), null)
        );
    }

    private static Stream<Arguments> assertDateTimePatternOrThrowTest() {
        return Stream.of(
                Arguments.of("", randomString()),
                Arguments.of("notFormatter1", "Unknown pattern letter: o"),
                Arguments.of("Europe/Kiev2", "Unknown pattern letter: r"),
                Arguments.of("dd.MM.yyyy HH:mm:ss", null),
                Arguments.of("dd/MM HH.mm", null)
        );
    }

    // =================================================================================================================
    // 1 assert complexity
    // =================================================================================================================
    @ParameterizedTest
    @MethodSource("assertNonNullOrThrowTest")
    void assertNonNullOrThrowTest(Object object, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertNonNullOrThrow(object, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertNonBlankOrThrowTest")
    void assertNonBlankOrThrowTest(String object, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertNonBlankOrThrow(object, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertNonEmptyOrThrowTest")
    void assertNonEmptyOrThrowTest(Collection<?> collection, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertNonEmptyOrThrow(collection, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertTrueOrThrowTest")
    void assertTrueOrThrowTest(boolean flag, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertTrueOrThrow(flag, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertFalseOrThrowTest")
    void assertFalseOrThrowTest(boolean flag, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertFalseOrThrow(flag, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertLongsArgs")
    void assertLongsTest(long value, boolean conditionPositive, boolean conditionPositiveOrZero, boolean conditionNegative, boolean conditionNegativeOrZero) {
        // Act
        var positive = catchThrowable(() -> JbstAsserts.assertPositiveOrThrow(value));
        var positiveOrZero = catchThrowable(() -> JbstAsserts.assertPositiveOrZeroOrThrow(value));
        var negative = catchThrowable(() -> JbstAsserts.assertNegativeOrThrow(value));
        var negativeOrZero = catchThrowable(() -> JbstAsserts.assertNegativeOrZeroOrThrow(value));

        // Assert
        if (conditionPositive) {
            assertThat(positive).isNull();
        } else {
            assertThat(positive).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionPositiveOrZero) {
            assertThat(positiveOrZero).isNull();
        } else {
            assertThat(positiveOrZero).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionNegative) {
            assertThat(negative).isNull();
        } else {
            assertThat(negative).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionNegativeOrZero) {
            assertThat(negativeOrZero).isNull();
        } else {
            assertThat(negativeOrZero).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("assertBigDecimalsArgs")
    void assertBigDecimalsTest(BigDecimal value, boolean conditionPositive, boolean conditionPositiveOrZero, boolean conditionNegative, boolean conditionNegativeOrZero) {
        // Act
        var positive = catchThrowable(() -> JbstAsserts.assertPositiveOrThrow(value));
        var positiveOrZero = catchThrowable(() -> JbstAsserts.assertPositiveOrZeroOrThrow(value));
        var negative = catchThrowable(() -> JbstAsserts.assertNegativeOrThrow(value));
        var negativeOrZero = catchThrowable(() -> JbstAsserts.assertNegativeOrZeroOrThrow(value));

        // Assert
        if (conditionPositive) {
            assertThat(positive).isNull();
        } else {
            assertThat(positive).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionPositiveOrZero) {
            assertThat(positiveOrZero).isNull();
        } else {
            assertThat(positiveOrZero).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionNegative) {
            assertThat(negative).isNull();
        } else {
            assertThat(negative).isInstanceOf(IllegalArgumentException.class);
        }

        if (conditionNegativeOrZero) {
            assertThat(negativeOrZero).isNull();
        } else {
            assertThat(negativeOrZero).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("assertBigDecimalsBetweenArgs")
    void assertBetweenTests(BigDecimal left, BigDecimal value, BigDecimal right, boolean excludedCondition, boolean includedCondition) {
        // Act
        var excluded = catchThrowable(() -> JbstAsserts.assertBetweenExcludedOrThrow(value, left, right));
        var included = catchThrowable(() -> JbstAsserts.assertBetweenIncludedOrThrow(value, left, right));

        // Assert
        if (excludedCondition) {
            assertThat(excluded).isNull();
        } else {
            assertThat(excluded).isInstanceOf(IllegalArgumentException.class);
        }

        if (includedCondition) {
            assertThat(included).isNull();
        } else {
            assertThat(included).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("requireNonNullOrThrowTest")
    void requireNonNullOrThrowTest(Object object, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> {
            // Act
            var actual = JbstAsserts.requireNonNullOrThrow(object, expectedErrorMessage);

            // Assert
            assertThat(actual).isEqualTo(object);
        });

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertSortedOrThrowTest")
    void assertSortedOrThrowTest(Collection<Integer> collection, Comparator<Integer> comparator, boolean isSorted) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertSortedOrThrow(collection, comparator));

        // Assert
        if (isSorted) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("assertUniqueOrThrowTest")
    void assertUniqueOrThrowTest(String check, String expectedErrorMessage) {
        // Arrange
        var options = new HashSet<>(Set.of("ONE", "TWO", "THREE"));

        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertUniqueOrThrow(options, check, new PropertyId("field-name")));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    // =================================================================================================================
    // 1+ asserts complexity
    // =================================================================================================================
    @ParameterizedTest
    @MethodSource("assertZoneIdOrThrowTest")
    void assertZoneIdOrThrowTest(String zoneId, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertZoneIdOrThrow(zoneId, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }

    @ParameterizedTest
    @MethodSource("assertDateTimePatternOrThrowTest")
    void assertDateTimePatternOrThrowTest(String dateTimePattern, String expectedErrorMessage) {
        // Act
        var throwable = catchThrowable(() -> JbstAsserts.assertDateTimePatternOrThrow(dateTimePattern, expectedErrorMessage));

        // Assert
        if (isNull(expectedErrorMessage)) {
            assertThat(throwable).isNull();
        } else {
            assertThat(throwable)
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(expectedErrorMessage);
        }
    }
}
