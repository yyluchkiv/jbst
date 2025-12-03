package jbst.foundation.domain.numbers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jbst.foundation.domain.numbers.LongUtility.toIntExactOrZeroOnOverflow;
import static org.assertj.core.api.Assertions.assertThat;

class LongUtilityTest {

    private static Stream<Arguments> toIntExactOrZeroOnOverflowTest() {
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
    @MethodSource("toIntExactOrZeroOnOverflowTest")
    void toIntExactOrZeroOnOverflowTest(long value, int expected) {
        // Act
        int actual = toIntExactOrZeroOnOverflow(value);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
