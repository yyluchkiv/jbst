package jbst.foundation.utilities.time;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class JbstTimeTest {

    private static Stream<Arguments> getStartOfMonthTestArgs() {
        return Stream.of(
                Arguments.of(1705474657000L, 1704067200000L),
                Arguments.of(1704059999000L, 1701388800000L)
        );
    }

    @MethodSource("getStartOfMonthTestArgs")
    @ParameterizedTest
    void getStartOfMonthTest(long timestamp, long expected) {
        // Act + Assert
        assertThat(JbstTime.getStartOfMonth(timestamp)).isEqualTo(expected);
    }
}
