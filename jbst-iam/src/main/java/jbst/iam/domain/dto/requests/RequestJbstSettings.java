package jbst.iam.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;

// TODO [YYL] add possible @Valid details
public record RequestJbstSettings(
        @NotNull JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {
}
