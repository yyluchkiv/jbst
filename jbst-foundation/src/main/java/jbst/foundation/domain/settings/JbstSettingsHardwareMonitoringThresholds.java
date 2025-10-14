package jbst.foundation.domain.settings;

import jbst.foundation.domain.hardware.monitoring.HardwareName;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public record JbstSettingsHardwareMonitoringThresholds(
        boolean enabled,
        Map<HardwareName, BigDecimal> values
) {

    public static JbstSettingsHardwareMonitoringThresholds hardcoded() {
        return new JbstSettingsHardwareMonitoringThresholds(
                true,
                new EnumMap<>(
                        Map.of(
                                HardwareName.CPU, new BigDecimal("80"),
                                HardwareName.HEAP, new BigDecimal("85"),
                                HardwareName.SERVER, new BigDecimal("90"),
                                HardwareName.SWAP, new BigDecimal("95"),
                                HardwareName.VIRTUAL, new BigDecimal("98")
                        )
                )
        );
    }
}
