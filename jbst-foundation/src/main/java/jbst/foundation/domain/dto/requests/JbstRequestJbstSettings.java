package jbst.foundation.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;

// TODO [YYL] add possible @Valid details
public record JbstRequestJbstSettings(
        @NotNull JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static JbstRequestJbstSettings hardcoded() {
        return new JbstRequestJbstSettings(
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
