package jbst.foundation.domain.numbers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static jbst.foundation.domain.numbers.JbstNumbers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JbstNumbersTest {

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
