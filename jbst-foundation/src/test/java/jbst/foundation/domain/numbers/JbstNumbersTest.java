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

import static java.math.BigDecimal.*;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.HUNDRED;
import static jbst.foundation.domain.constants.JbstConstants.Numbers.BigDecimals.TWO;
import static jbst.foundation.domain.numbers.JbstNumbers.*;
import static jbst.foundation.domain.random.JbstRandom.randomBigDecimalGreaterThanZero;
import static jbst.foundation.domain.random.JbstRandom.randomBigDecimalLessThanZero;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JbstNumbersTest {
    // =================================================================================================================
    // BIGDECIMAL(s)
    // =================================================================================================================
    private static Stream<Arguments> isArgs() {
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
    @ParameterizedTest
    @MethodSource("isArgs")
    void isTest(BigDecimal number1, String operator, BigDecimal number2, boolean expected) {
        // Act + Assert
        assertThat(is(number1, operator, number2)).isEqualTo(expected);
    }

    private static Stream<Arguments> isExceptionArgs() {
        return Stream.of(
                Arguments.of(TWO, "=", TWO, "Available operators: [==, >, >=, <, <=]"),
                Arguments.of(TWO, "!=", TWO, "Available operators: [==, >, >=, <, <=]")
        );
    }
    @ParameterizedTest
    @MethodSource("isExceptionArgs")
    void isExceptionTest(BigDecimal number1, String operator, BigDecimal number2, String expected) {
        // Act
        var throwable = catchThrowable(() -> is(number1, operator, number2));

        // Assert
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo(expected);
    }

    private static Stream<Arguments> equalsApproximatelyArgs() {
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
    @ParameterizedTest
    @MethodSource("equalsApproximatelyArgs")
    void equalsApproximatelyTest(BigDecimal n1, BigDecimal n2, BigDecimal proximity, boolean expected) {
        // Act
        var actual = equalsApproximately(n1, n2, proximity);

        // Assert
        assertThat(actual)
                .withFailMessage("{n1, n2, proximity} = %s, %s, %s", n1, n2, proximity)
                .isEqualTo(expected);
    }

    private static Stream<Arguments> inRangeArgs() {
        return Stream.of(
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(ZERO, new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), ZERO), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("1"), new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("-1")), false)
        );
    }
    @ParameterizedTest
    @MethodSource("inRangeArgs")
    void inRangeTest(BigDecimal value, TupleRange<BigDecimal> range, boolean expected) {
        // Act + Assert
        assertThat(inRange(value, range)).isEqualTo(expected);
    }

    private static Stream<Arguments> inRangeClosedArgs() {
        return Stream.of(
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(ZERO, new BigDecimal("2")), true),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), ZERO), true),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("1"), new BigDecimal("2")), false),
                Arguments.of(ZERO, new TupleRange<>(new BigDecimal("-2"), new BigDecimal("-1")), false)
        );
    }
    @ParameterizedTest
    @MethodSource("inRangeClosedArgs")
    void inRangeClosedTest(BigDecimal value, TupleRange<BigDecimal> range, boolean expected) {
        // Act + Assert
        assertThat(inRangeClosed(value, range)).isEqualTo(expected);
    }

    private static Stream<Arguments> isZeroArgs() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }
    @ParameterizedTest
    @MethodSource("isZeroArgs")
    void isZeroTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isZero(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isNullOrZeroArgs() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }
    @ParameterizedTest
    @MethodSource("isNullOrZeroArgs")
    void isNullOrZeroTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isNullOrZero(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isHundredArgs() {
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
    @ParameterizedTest
    @MethodSource("isHundredArgs")
    void isHundredTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isHundred(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isPositiveArgs() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), true),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }
    @ParameterizedTest
    @MethodSource("isPositiveArgs")
    void isPositiveTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isPositive(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isPositiveOrZeroArgs() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), true),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), false)
        );
    }
    @ParameterizedTest
    @MethodSource("isPositiveOrZeroArgs")
    void isPositiveOrZeroTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isPositiveOrZero(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isNegativeArgs() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, false),
                Arguments.of(randomBigDecimalLessThanZero(), true)
        );
    }
    @ParameterizedTest
    @MethodSource("isNegativeArgs")
    void isNegativeTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isNegative(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> isNegativeOrZeroArgs() {
        return Stream.of(
                Arguments.of(randomBigDecimalGreaterThanZero(), false),
                Arguments.of(ZERO, true),
                Arguments.of(randomBigDecimalLessThanZero(), true)
        );
    }
    @ParameterizedTest
    @MethodSource("isNegativeOrZeroArgs")
    void isNegativeOrZeroTest(BigDecimal value, boolean expected) {
        // Act + Assert
        assertThat(isNegativeOrZero(value)).isEqualTo(expected);
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

    private static Stream<Arguments> getNumberOfDigitsAfterTheDecimalPointOrZeroArgs() {
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
    @ParameterizedTest
    @MethodSource("getNumberOfDigitsAfterTheDecimalPointOrZeroArgs")
    void getNumberOfDigitsAfterTheDecimalPointOrZeroTest(BigDecimal value, int expected) {
        // Act + Assert
        assertThat(getNumberOfDigitsAfterTheDecimalPointOrZero(value)).isEqualTo(expected);
    }

    private static Stream<Arguments> getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroArgs() {
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
    @MethodSource("getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroArgs")
    void getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZeroTest(BigDecimal number, int expected) {
        // Act + Assert
        assertThat(getNumberOfDigitsAfterTheDecimalPointIncludingTrailingZerosOrZero(number)).isEqualTo(expected);
    }

    // =================================================================================================================
    // LONG(s)
    // =================================================================================================================
    private static Stream<Arguments> toIntExactOrZeroOnOverflowArgs() {
        return Stream.of(
                Arguments.of(0L, 0),
                Arguments.of(200L, 200),
                Arguments.of(1000L, 1000),
                Arguments.of(Integer.MAX_VALUE, 2147483647),
                Arguments.of(Long.MAX_VALUE, 0),
                Arguments.of(Long.MAX_VALUE - 1, 0),
                Arguments.of(Long.MAX_VALUE - 1000, 0),
                Arguments.of(Long.MAX_VALUE - Integer.MAX_VALUE, 0),
                Arguments.of(Long.MIN_VALUE + 1000, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("toIntExactOrZeroOnOverflowArgs")
    void toIntExactOrZeroOnOverflowTest(long value, int expected) {
        // Act + Assert
        assertThat(toIntExactOrZeroOnOverflow(value)).isEqualTo(expected);
    }

    // =================================================================================================================
    // DIVISION(s)
    // =================================================================================================================
    private static Stream<Arguments> divideArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 3, BigDecimal.valueOf(3.333)),
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 4, BigDecimal.valueOf(3.3333)),
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 5, BigDecimal.valueOf(3.33333))
        );
    }
    @ParameterizedTest
    @MethodSource("divideArgs")
    void divideTest(BigDecimal divider, BigDecimal divisor, int scale, BigDecimal expected) {
        BigDecimal actual;
        if (scale == DEFAULT_SCALE) {
            // Act
            actual = divide(divider, divisor);
        } else {
            // Act
            actual = divide(divider, divisor, scale);
        }
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> divideOrZeroArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 3, BigDecimal.valueOf(3.333)),
                Arguments.of(BigDecimal.valueOf(10), null, 4, BigDecimal.ZERO),
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.ZERO, 5, BigDecimal.ZERO)
        );
    }
    @ParameterizedTest
    @MethodSource("divideOrZeroArgs")
    void divideOrZeroTest(BigDecimal divider, BigDecimal divisor, int scale, BigDecimal expected) {
        // Act + Assert
        assertThat(divideOrZero(divider, divisor, scale)).isEqualTo(expected);
    }

    private static Stream<Arguments> divideOrOneArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 3, BigDecimal.valueOf(3.333)),
                Arguments.of(BigDecimal.valueOf(10), null, 4, BigDecimal.ONE),
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.ZERO, 5, BigDecimal.ONE),
                Arguments.of(BigDecimal.valueOf(10), null, 5, BigDecimal.ONE)
        );
    }
    @ParameterizedTest
    @MethodSource("divideOrOneArgs")
    void divideOrOneTest(BigDecimal divider, BigDecimal divisor, int scale, BigDecimal expected) {
        // Arrange
        if (isNull(divisor)) {
            divisor = mock(BigDecimal.class);
            when(divisor.compareTo(any(BigDecimal.class))).thenThrow(new RuntimeException());
        }

        // Act
        var actual = divideOrOne(divider, divisor, scale);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> divideOrFallbackArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10), BigDecimal.valueOf(3), 3, TEN, BigDecimal.valueOf(3.333)),
                Arguments.of(BigDecimal.valueOf(10), null, 4, TEN, TEN),
                Arguments.of(BigDecimal.valueOf(10), ZERO, 5, TEN, TEN),
                Arguments.of(BigDecimal.valueOf(10), null, 5, TEN, TEN)
        );
    }
    @ParameterizedTest
    @MethodSource("divideOrFallbackArgs")
    void divideOrFallbackTest(BigDecimal divider, BigDecimal divisor, int scale, BigDecimal fallback, BigDecimal expected) {
        // Arrange
        if (isNull(divisor)) {
            divisor = mock(BigDecimal.class);
            when(divisor.compareTo(any(BigDecimal.class))).thenThrow(new RuntimeException());
        }

        // Act
        var actual = divideOrFallback(divider, divisor, scale, fallback);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    // =================================================================================================================
    // READABILITY
    // =================================================================================================================
    private static Stream<Arguments> readableNumbersArgs() {
        return Stream.of(
                // Null + Zero
                Arguments.of(null, "0.00"),
                Arguments.of(BigDecimal.ZERO, "0.00"),

                // -1K < N < 1K
                Arguments.of(BigDecimal.valueOf(-999.99), "-999.99"),
                Arguments.of(BigDecimal.valueOf(-500), "-500.00"),
                Arguments.of(BigDecimal.valueOf(-5), "-5.00"),
                Arguments.of(BigDecimal.valueOf(5), "5.00"),
                Arguments.of(BigDecimal.valueOf(500), "500.00"),
                Arguments.of(BigDecimal.valueOf(999.99), "999.99"),

                // N =< -1B
                // N >= 1B
                Arguments.of(BigDecimal.valueOf(-1750000000), "-1.75B"),
                Arguments.of(BigDecimal.valueOf(-1500000000), "-1.5B"),
                Arguments.of(BigDecimal.valueOf(-1000000000), "-1B"),
                Arguments.of(BigDecimal.valueOf(1000000000), "1B"),
                Arguments.of(BigDecimal.valueOf(1500000000), "1.5B"),
                Arguments.of(BigDecimal.valueOf(1750000000), "1.75B"),

                // N >= 1M
                Arguments.of(BigDecimal.valueOf(-12345678.555), "-12.35M"),
                Arguments.of(BigDecimal.valueOf(-12345678), "-12.35M"),
                Arguments.of(BigDecimal.valueOf(-1000000), "-1M"),
                Arguments.of(BigDecimal.valueOf(1000000), "1M"),
                Arguments.of(BigDecimal.valueOf(12345678), "12.35M"),
                Arguments.of(BigDecimal.valueOf(12345678.999), "12.35M"),

                // N >= 1K
                Arguments.of(BigDecimal.valueOf(-125564), "-125.56K"),
                Arguments.of(BigDecimal.valueOf(-1150.1111), "-1.15K"),
                Arguments.of(BigDecimal.valueOf(-1150.7777), "-1.15K"),
                Arguments.of(new BigDecimal("-1150.7777"), "-1.15K"),
                Arguments.of(BigDecimal.valueOf(-1150), "-1.15K"),
                Arguments.of(BigDecimal.valueOf(-1050), "-1.05K"),
                Arguments.of(BigDecimal.valueOf(-1005), "-1.01K"),
                Arguments.of(BigDecimal.valueOf(-1004), "-1K"),
                Arguments.of(BigDecimal.valueOf(-1000), "-1K"),
                Arguments.of(BigDecimal.valueOf(1000), "1K"),
                Arguments.of(BigDecimal.valueOf(1004), "1K"),
                Arguments.of(BigDecimal.valueOf(1005), "1.01K"),
                Arguments.of(BigDecimal.valueOf(1050), "1.05K"),
                Arguments.of(BigDecimal.valueOf(1150), "1.15K"),
                Arguments.of(BigDecimal.valueOf(1150.1111), "1.15K"),
                Arguments.of(BigDecimal.valueOf(1150.7777), "1.15K"),
                Arguments.of(new BigDecimal("1150.7777"), "1.15K"),
                Arguments.of(BigDecimal.valueOf(125564), "125.56K")
        );
    }
    @ParameterizedTest
    @MethodSource("readableNumbersArgs")
    void getReadableNumberTest(BigDecimal number, String expectedReadableNumber) {
        // Act + Assert
        assertThat(getReadableNumber(number)).isEqualTo(expectedReadableNumber);
    }

    // =================================================================================================================
    // FORMAT(s)
    // =================================================================================================================
    private static Stream<Arguments> formatArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 3, "5 941 306,042"),
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 4, "5 941 306,0421"),
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 5, "5 941 306,04213")
        );
    }
    @ParameterizedTest
    @MethodSource("formatArgs")
    void formatTest(BigDecimal value, int scale, String expected) {
        String actual;
        if (scale == DEFAULT_SCALE) {
            // Act
            actual = format(value);

            // Assert
            var actual2 = format(value, scale);
            assertThat(actual2).isEqualTo(expected);
        } else {
            // Act
            actual = format(value, scale);
        }
        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    // =================================================================================================================
    // SCALING
    // =================================================================================================================
    private static Stream<Arguments> scaleArgs() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 3, BigDecimal.valueOf(5941306.042)),
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 4, BigDecimal.valueOf(5941306.0421)),
                Arguments.of(BigDecimal.valueOf(5941306.04212988495091641), 5, BigDecimal.valueOf(5941306.04213))
        );
    }
    @ParameterizedTest
    @MethodSource("scaleArgs")
    void scaleTest(BigDecimal value, Integer scale, BigDecimal expected) {
        BigDecimal actual;
        if (scale == DEFAULT_SCALE) {
            // Act
            actual = scale(value);
        } else {
            // Act
            actual = scale(value, scale);
        }
        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
