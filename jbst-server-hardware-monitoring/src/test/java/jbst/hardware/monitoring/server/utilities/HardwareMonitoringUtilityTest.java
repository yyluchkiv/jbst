package jbst.hardware.monitoring.server.utilities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HardwareMonitoringUtilityTest {

    @Test
    void getSystemMemoriesNotNullTest() {
        // Act
        var actual = HardwareMonitoringUtility.getSystemMemories();

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.global()).isNotNull();
        assertThat(actual.cpu()).isNotNull();
    }
}
