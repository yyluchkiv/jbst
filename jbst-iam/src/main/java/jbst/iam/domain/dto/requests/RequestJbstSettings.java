package jbst.iam.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;

// TODO [YYL] add possible @Valid details
public record RequestJbstSettings(
        @NotNull JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static RequestJbstSettings hardcoded() {
        return new RequestJbstSettings(
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
