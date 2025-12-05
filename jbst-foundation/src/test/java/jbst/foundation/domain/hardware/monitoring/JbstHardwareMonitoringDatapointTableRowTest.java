package jbst.foundation.domain.hardware.monitoring;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.*;
import static org.assertj.core.api.Assertions.assertThat;

class JbstHardwareMonitoringDatapointTableRowTest {

    private static Stream<Arguments> constructorTest() {
        return Stream.of(
                Arguments.of(Map.of(), false),
                Arguments.of(Map.of(JbstHardwareName.HEAP, randomBigDecimalGreaterThanZeroByBounds(50L, 100L)), false),
                Arguments.of(Map.of(JbstHardwareName.CPU, randomBigDecimalGreaterThanZeroByBounds(50L, 100L)), false),
                Arguments.of(Map.of(JbstHardwareName.CPU, randomBigDecimalGreaterThanZeroByBounds(5L, 10L)), true)
        );
    }

    @ParameterizedTest
    @MethodSource("constructorTest")
    void constructorTest(Map<JbstHardwareName, BigDecimal> thresholds, boolean expected) {
        // Act
        var actual = new JbstHardwareMonitoringDatapointTableRow(
                JbstHardwareName.CPU,
                randomLongGreaterThanZero(),
                randomBigDecimalGreaterThanZeroByBounds(20L, 30L),
                randomString(),
                thresholds
        );

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getHardwareName()).isNotNull();
        assertThat(actual.getTimestamp()).isNotZero();
        assertThat(actual.getUsage()).isNotNull();
        assertThat(actual.getValue()).isNotNull();
        assertThat(actual.isThresholdReached()).isEqualTo(expected);
    }
}
