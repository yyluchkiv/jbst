package jbst.foundation.utilities.numbers;

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
import static jbst.foundation.utilities.numbers.BigDecimalUtility.*;
import static jbst.foundation.utilities.random.RandomUtility.randomBigDecimalGreaterThanZero;
import static jbst.foundation.utilities.random.RandomUtility.randomBigDecimalLessThanZero;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class BigDecimalUtilityTest {

    private static Stream<Arguments> areValuesEqualsTest() {
        return Stream.of(
                Arguments.of(null, null, true),
                Arguments.of(null, ZERO, false),
                Arguments.of(ZERO, null, false),
                Arguments.of(ZERO, ZERO, true),
                Arguments.of(ZERO, new BigDecimal("0"), true),
                Arguments.of(ZERO, new BigDecimal("0.00"), true),
                Arguments.of(ZERO, new BigDecimal(0L), true),
                Arguments.of(ZERO, new BigDecimal("1"), false),
                Arguments.of(ZERO, new BigDecimal("1.0"), false)
        );
    }

    private static Stream<Arguments> isFirstValueGreaterExceptionTest() {
        return Stream.of(
                Arguments.of(null, null, "Attribute `number1` is invalid"),
                Arguments.of(null, ZERO, "Attribute `number1` is invalid"),
                Arguments.of(ZERO, null, "Attribute `number2` is invalid")
        );
    }

    private static Stream<Arguments> isFirstValueGreaterTest() {
        return Stream.of(
                Arguments.of(ZERO, new BigDecimal("-1"), true),
                Arguments.of(ZERO, new BigDecimal("1"), false),
                Arguments.of(ZERO, ZERO, false)
        );
    }

    private static Stream<Arguments> isFirstValueGreaterOrEqualExceptionTest() {
        return Stream.of(
                Arguments.of(null, null, "Attribute `number1` is invalid"),
                Arguments.of(null, ZERO, "Attribute `number1` is invalid"),
                Arguments.of(ZERO, null, "Attribute `number2` is invalid")
        );
    }

    private static Stream<Arguments> isFirstValueGreaterOrEqualTest() {
        return Stream.of(
                Arguments.of(ZERO, new BigDecimal("-1"), true),
                Arguments.of(ZERO, new BigDecimal("1"), false),
                Arguments.of(ZERO, ZERO, true)
        );
    }

    private static Stream<Arguments> isFirstValueLesserExceptionTest() {
        return Stream.of(
                Arguments.of(null, null, "Attribute `number1` is invalid"),
                Arguments.of(null, ZERO, "Attribute `number1` is invalid"),
                Arguments.of(ZERO, null, "Attribute `number2` is invalid")
        );
    }

    private static Stream<Arguments> isFirstValueLesserTest() {
        return Stream.of(
                Arguments.of(ZERO, new BigDecimal("-1"), false),
                Arguments.of(ZERO, new BigDecimal("1"), true),
                Arguments.of(ZERO, ZERO, false)
        );
    }

    private static Stream<Arguments> isFirstValueLesserOrEqualExceptionTest() {
        return Stream.of(
                Arguments.of(null, null, "Attribute `number1` is invalid"),
                Arguments.of(null, ZERO, "Attribute `number1` is invalid"),
                Arguments.of(ZERO, null, "Attribute `number2` is invalid")
        );
    }

    private static Stream<Arguments> isFirstValueLesserOrEqualTest() {
        return Stream.of(
                Arguments.of(ZERO, new BigDecimal("-1"), false),
                Arguments.of(ZERO, new BigDecimal("1"), true),
                Arguments.of(ZERO, ZERO, true)
        );
    }

    private static Stream<Arguments> inRangeExceptionTest() {
        return Stream.of(
                Arguments.of(null, new TupleRange<>(ZERO, ZERO), "Attribute `number` is invalid"),
                Arguments.of(ZERO, null, "Attribute `range` is invalid")
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

    private static Stream<Arguments> inRangeClosedExceptionTest() {
        return Stream.of(
                Arguments.of(null, new TupleRange<>(ZERO, ZERO), "Attribute `number` is invalid"),
                Arguments.of(ZERO, null, "Attribute `range` is invalid")
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

    private static Stream<Arguments> isOneHundredTest() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), false),
                Arguments.of(HUNDRED, true),
                Arguments.of(new BigDecimal("100"), true),
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
    @MethodSource("areValuesEqualsTest")
    void areValuesEqualsTest(BigDecimal value1, BigDecimal value2, boolean expected) {
        // Act
        var actual = areValuesEquals(value1, value2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueGreaterExceptionTest")
    void isFirstValueGreaterExceptionTest(BigDecimal value1, BigDecimal value2, String expected) {
        // Act
        var throwable = catchThrowable(() -> isFirstValueGreater(value1, value2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueGreaterTest")
    void isFirstValueGreaterTest(BigDecimal value1, BigDecimal value2, boolean expected) {
        // Act
        var actual = isFirstValueGreater(value1, value2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueGreaterOrEqualExceptionTest")
    void isFirstValueGreaterOrEqualExceptionTest(BigDecimal value1, BigDecimal value2, String expected) {
        // Act
        var throwable = catchThrowable(() -> isFirstValueGreaterOrEqual(value1, value2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueGreaterOrEqualTest")
    void isFirstValueGreaterOrEqualTest(BigDecimal value1, BigDecimal value2, boolean expected) {
        // Act
        var actual = isFirstValueGreaterOrEqual(value1, value2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueLesserExceptionTest")
    void isFirstValueLesserExceptionTest(BigDecimal value1, BigDecimal value2, String expected) {
        // Act
        var throwable = catchThrowable(() -> isFirstValueLesser(value1, value2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueLesserTest")
    void isFirstValueLesserTest(BigDecimal value1, BigDecimal value2, boolean expected) {
        // Act
        var actual = isFirstValueLesser(value1, value2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueLesserOrEqualExceptionTest")
    void isFirstValueLesserOrEqualExceptionTest(BigDecimal value1, BigDecimal value2, String expected) {
        // Act
        var throwable = catchThrowable(() -> isFirstValueLesserOrEqual(value1, value2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("isFirstValueLesserOrEqualTest")
    void isFirstValueLesserOrEqualTest(BigDecimal value1, BigDecimal value2, boolean expected) {
        // Act
        var actual = isFirstValueLesserOrEqual(value1, value2);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("inRangeExceptionTest")
    void inRangeExceptionTest(BigDecimal value, TupleRange<BigDecimal> range, String expected) {
        // Act
        var throwable = catchThrowable(() -> inRange(value, range));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
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
    @MethodSource("inRangeClosedExceptionTest")
    void inRangeClosedExceptionTest(BigDecimal value, TupleRange<BigDecimal> range, String expected) {
        // Act
        var throwable = catchThrowable(() -> inRangeClosed(value, range));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
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
    @MethodSource("isOneHundredTest")
    void isOneHundredTest(BigDecimal value, boolean expected) {
        // Act
        var actual = isOneHundred(value);

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
