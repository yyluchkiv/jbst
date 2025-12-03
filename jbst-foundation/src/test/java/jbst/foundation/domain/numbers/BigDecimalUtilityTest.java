package jbst.foundation.domain.numbers;

import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.tuples.TupleRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.TWO;
import static jbst.foundation.domain.numbers.BigDecimalUtility.*;
import static jbst.foundation.domain.random.JbstRandom.randomBigDecimalGreaterThanZero;
import static jbst.foundation.domain.random.JbstRandom.randomBigDecimalLessThanZero;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class BigDecimalUtilityTest {

    private static Stream<Arguments> isExceptionTest() {
        return Stream.of(
                Arguments.of(TWO, "=", TWO, "Available operators: [==, >, >=, <, <=]"),
                Arguments.of(TWO, "!=", TWO, "Available operators: [==, >, >=, <, <=]")
        );
    }

    private static Stream<Arguments> isTest() {
        return Stream.of(
                Arguments.of(null, "==", null, false),
                Arguments.of(null, "==", TWO, false),
                Arguments.of(TWO, "==", null, false),
                Arguments.of(TWO, "==", TWO, true),
                Arguments.of(TWO, "==", HUNDRED, false),
                Arguments.of(HUNDRED, "==", TWO, false),
                Arguments.of(HUNDRED, "==", HUNDRED, true),
                Arguments.of(TWO, ">", TWO, false),
                Arguments.of(TWO, ">=", TWO, true),
                Arguments.of(TWO, "<", TWO, false),
                Arguments.of(TWO, "<=", TWO, true),
                Arguments.of(TWO, ">", HUNDRED, false),
                Arguments.of(TWO, ">=", HUNDRED, false),
                Arguments.of(TWO, "<", HUNDRED, true),
                Arguments.of(TWO, "<=", HUNDRED, true),
                Arguments.of(HUNDRED, ">", TWO, true),
                Arguments.of(HUNDRED, ">=", TWO, true),
                Arguments.of(HUNDRED, "<", TWO, false),
                Arguments.of(HUNDRED, "<=", TWO, false)
        );
    }

    private static Stream<Arguments> equalsApproximatelyTest() {
        return Stream.of(
                // 100 == 100
                Arguments.of(HUNDRED, HUNDRED, new BigDecimal("0.1"), true),
                Arguments.of(HUNDRED, HUNDRED, new BigDecimal("0.5"), true),
                Arguments.of(HUNDRED, HUNDRED, new BigDecimal("0.001"), true),
                Arguments.of(HUNDRED, HUNDRED, new BigDecimal("150"), true),

                // <0.5% true
                Arguments.of(HUNDRED, new BigDecimal("100.4"), new BigDecimal("0.5"), true),
                Arguments.of(HUNDRED, new BigDecimal("99.5"), new BigDecimal("0.5"), true),

                // >0.5% false
                Arguments.of(HUNDRED, new BigDecimal("100.6"), new BigDecimal("0.5"), false),
                Arguments.of(HUNDRED, new BigDecimal("99.4"), new BigDecimal("0.5"), false),

                // <1% true
                Arguments.of(HUNDRED, new BigDecimal("101.0"), new BigDecimal("1.0"), true),
                Arguments.of(HUNDRED, new BigDecimal("99.0"), new BigDecimal("1.0"), true),

                // >1% false
                Arguments.of(HUNDRED, new BigDecimal("101.1"), new BigDecimal("1.0"), false),
                Arguments.of(HUNDRED, new BigDecimal("98.9"), new BigDecimal("1.0"), false),

                // negative values, same sign
                Arguments.of(new BigDecimal("-200"), new BigDecimal("-198"), new BigDecimal("1.0"), true),
                Arguments.of(new BigDecimal("-200"), new BigDecimal("-196"), new BigDecimal("1.0"), false),

                // mixed signs
                Arguments.of(HUNDRED, HUNDRED.negate(), new BigDecimal("200.0"), true),
                Arguments.of(HUNDRED, HUNDRED.negate(), new BigDecimal("100.0"), false),
                Arguments.of(HUNDRED, HUNDRED.negate(), new BigDecimal("99.9"), false),

                // 0 == 0
                Arguments.of(ZERO, ZERO, new BigDecimal("0.1"), true),

                // 0 != non-zero
                Arguments.of(ZERO, new BigDecimal("0.1"), new BigDecimal("0.1"), false),
                Arguments.of(new BigDecimal("0.01"), ZERO, new BigDecimal("0.1"), false)
        );
    }

    private static Stream<Arguments> inRangeTest() {
        return Stream.of(
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(ZERO, new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), ZERO), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("1"), new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("-1")), false)
        );
    }

    private static Stream<Arguments> inRangeClosedTest() {
        return Stream.of(
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(ZERO, new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), ZERO), true),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("1"), new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("-1")), false)
        );
    }

    private static Stream<Arguments> isZeroTest() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }

    private static Stream<Arguments> isNullOrZeroTest() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }

    private static Stream<Arguments> isHundredTest() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), false),
                Arguments.of(HUNDRED, true),
                Arguments.of(HUNDRED, true),
                Arguments.of(new BigDecimal("100.00"), true),
                Arguments.of(new BigDecimal("100.00000"), true)
        );
    }

    private static Stream<Arguments> isPositiveTest() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), true),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }

    private static Stream<Arguments> isPositiveOrZeroTest() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), true),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }

    private static Stream<Arguments> isNegativeTest() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), true)
        );
    }

    private static Stream<Arguments> isNegativeOrZeroTest() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), true)
        );
    }

    private static Stream<Arguments> getNumberOfDigitsAfterTheDecimalPointOrZeroTest() {
        return Stream.of(
                Arguments.of(ZERO, 0),
                Arguments.of(TWO, 0),
                Arguments.of(HUNDRED, 0),
                Arguments.of(new BigDecimal("33"), 0),
                Arguments.of(new BigDecimal("1.1"), 1),
                Arguments.of(new BigDecimal("1.11"), 2),
                Arguments.of(new BigDecimal("1.111"), 3),
                Arguments.of(new BigDecimal("1.111111"), 6),
                Arguments.of(new BigDecimal("1.00"), 0),
                Arguments.of(new BigDecimal("1.0000"), 0),
                Arguments.of(new BigDecimal("1.00000"), 0),
                Arguments.of(new BigDecimal("1.000000000000000000000"), 0),
                Arguments.of(new BigDecimal("1.0000000000000000000001"), 22)
        );
    }

    private static Stream<Arguments> getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroTest() {
        return Stream.of(
                Arguments.of(new BigDecimal("-244.5444"), 4),
                Arguments.of(new BigDecimal("0"), 0),
                Arguments.of(new BigDecimal("2646"), 0),
                Arguments.of(new BigDecimal("2646.0"), 1),
                Arguments.of(new BigDecimal("2646.01"), 2),
                Arguments.of(new BigDecimal("2646.010"), 3),
                Arguments.of(new BigDecimal("2646.01001"), 5)
        );
    }

    @ParameterizedTest
    @MethodSource("isExceptionTest")
    void isExceptionTest(BigDecimal number1, String operator, BigDecimal number2, String expected) {
        // Act
        var throwable = catchThrowable(() -> is(number1, operator, number2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isTest")
    void isTest(BigDecimal number1, String operator, BigDecimal number2, boolean expected) {
        // Act
        var actual = is(number1, operator, number2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("equalsApproximatelyTest")
    void equalsApproximatelyTest(BigDecimal n1, BigDecimal n2, BigDecimal proximity, boolean expected) {
        // Act
        var actual = equalsApproximately(n1, n2, proximity);

        // Assert
        assertThat(actual)
                .withFailMessage("{n1, n2, proximity} = %s, %s, %s", n1, n2, proximity)
                .isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("inRangeTest")
    void inRangeTest(BigDecimal value, TupleRange<BigDecimal> range, boolean expected) {
        // Act
        var actual = inRange(value, range);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("inRangeClosedTest")
    void inRangeClosedTest(BigDecimal value, TupleRange<BigDecimal> range, boolean expected) {
        // Act
        var actual = inRangeClosed(value, range);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isZeroTest")
    void isZeroTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isZero(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isNullOrZeroTest")
    void isNullOrZeroTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isNullOrZero(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isHundredTest")
    void isHundredTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isHundred(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isPositiveTest")
    void isPositiveTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isPositive(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isPositiveOrZeroTest")
    void isPositiveOrZeroTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isPositiveOrZero(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isNegativeTest")
    void isNegativeTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isNegative(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isNegativeOrZeroTest")
    void isNegativeOrZeroTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isNegativeOrZero(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void absOrZeroTest() {
        // Arrange
        var positive = randomBigDecimalGreaterThanZero();
        var negative = randomBigDecimalLessThanZero();
        List<Tuple2<BigDecimal, BigDecimal>> cases = new ArrayList<>();
        cases.add(new Tuple2<>(null, ZERO));
        cases.add(new Tuple2<>(ZERO, ZERO));
        cases.add(new Tuple2<>(positive, positive));
        cases.add(new Tuple2<>(negative, negative.multiply(ONE.negate())));

        cases.forEach(item -> {
            // Arrange
            var value = item.a();
            var expected = item.b();

            // Act
            var actual = absOrZero(value);

            // Assert
            assertThat(actual).isEqualTo(expected);
        });
    }

    @ParameterizedTest
    @MethodSource("getNumberOfDigitsAfterTheDecimalPointOrZeroTest")
    void getNumberOfDigitsAfterTheDecimalPointOrZeroTest(BigDecimal value, int expected) {
        // Act
        var actual = getNumberOfDigitsAfterTheDecimalPointOrZero(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroTest")
    void getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroTest(BigDecimal number, int expected) {
        // Act
        var actual = getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZero(number);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
