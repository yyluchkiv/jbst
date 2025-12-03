package jbst.foundation.domain.numbers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static jbst.foundation.domain.numbers.NumbersUtility.getReadableNumber;
import static org.assertj.core.api.Assertions.assertThat;

class NumbersUtilityTest {
    private static Stream<Arguments> readableNumbers() {
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
    @MethodSource("readableNumbers")
    void getReadableNumberTest(BigDecimal number, String expectedReadableNumber) {
        // Act
        var actualReadableNumber = getReadableNumber(number);

        // Assert
        assertThat(actualReadableNumber).isEqualTo(expectedReadableNumber);
    }
}
