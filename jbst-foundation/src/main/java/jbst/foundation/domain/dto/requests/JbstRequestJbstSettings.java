package jbst.foundation.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;

public record JbstRequestJbstSettings(
        @NotNull JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static JbstRequestJbstSettings hardcoded() {
        return new JbstRequestJbstSettings(
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
