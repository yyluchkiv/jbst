package jbst.foundation.domain.settings;

import jbst.foundation.domain.hardware.monitoring.JbstHardwareName;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public record JbstSettingsHardwareMonitoringThresholds(
        boolean enabled,
        Map<JbstHardwareName, BigDecimal> values
) {

    public static JbstSettingsHardwareMonitoringThresholds hardcoded() {
        return new JbstSettingsHardwareMonitoringThresholds(
                true,
                new EnumMap<>(
                        Map.of(
                                JbstHardwareName.CPU, new BigDecimal("80"),
                                JbstHardwareName.HEAP, new BigDecimal("85"),
                                JbstHardwareName.SERVER, new BigDecimal("90"),
                                JbstHardwareName.SWAP, new BigDecimal("95"),
                                JbstHardwareName.VIRTUAL, new BigDecimal("98")
                        )
                )
        );
    }
}
