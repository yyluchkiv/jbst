package jbst.foundation.domain.databases;

import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;

public record JbstSettings(
        String createdUTC,
        String updatedUTC,
        JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {
    public static JbstSettings hardcoded() {
        return new JbstSettings(
                "ops1 @ 21-10-2025 10:10",
                "ops2 @ 22-10-2025 11:11",
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
