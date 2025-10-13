package jbst.foundation.domain.properties.utilties;

import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.hardware.monitoring.HardwareName;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.foundation.utilities.collections.CollectorUtility;
import jbst.foundation.utilities.enums.EnumUtility;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class SettingsAsserterAndPrinterTest {

    @Test
    void hardwareMonitoringConfigsDisabledTest() {
        // Arrange
        var hardwareMonitoringConfigs = JbstSettingsHardwareMonitoringThresholds.disabled();

        // Act
        JbstSettingsHardwareMonitoringThresholds.disabled().assertProperties(new PropertyId("hardwareMonitoringConfigs"));

        // Assert
        var values = hardwareMonitoringConfigs.getValues();
        assertThat(values).hasSize(5);
        assertThat(values.keySet()).isEqualTo(EnumUtility.set(HardwareName.class));
        assertThat(values.values().stream().distinct().collect(CollectorUtility.toSingleton())).isEqualTo(ZERO);
    }

    @Test
    void hardwareMonitoringConfigsExceptionTest() {
        // Arrange
        var hardwareMonitoringConfigs = new JbstSettingsHardwareMonitoringThresholds(
                true,
                Map.ofEntries(
                    entry(HardwareName.CPU, new BigDecimal("80")),
                    entry(HardwareName.HEAP, new BigDecimal("85"))
                )
        );

        // Act
        var throwable = catchThrowable(() -> hardwareMonitoringConfigs.assertProperties(new PropertyId("hardwareMonitoringConfigs")));

        // Assert
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(throwable.getMessage()).isEqualTo("Property \"\u001B[31mhardwareMonitoringConfigs.thresholdsConfigs\u001B[0m\" is invalid. Options: \"[CPU, HEAP, SERVER, SWAP, VIRTUAL]\". Required: \"[CPU, HEAP]\". Disjunction: \"[\u001B[31mSERVER, SWAP, VIRTUAL\u001B[0m]\"");
    }

    @Test
    void hardwareMonitoringConfigsTest() {
        // Act
        JbstSettingsHardwareMonitoringThresholds.hardcoded().assertProperties(new PropertyId("hardwareMonitoringConfigs"));
        JbstSettingsHardwareMonitoringThresholds.hardcoded().printProperties(new PropertyId("hardwareMonitoringConfigs"));

        // Assert
        var values = JbstSettingsHardwareMonitoringThresholds.hardcoded().getValues();
        assertThat(values).hasSize(5);
        assertThat(values.keySet()).isEqualTo(EnumUtility.set(HardwareName.class));
        assertThat(new HashSet<>(values.values())).hasSize(5);
    }
}
