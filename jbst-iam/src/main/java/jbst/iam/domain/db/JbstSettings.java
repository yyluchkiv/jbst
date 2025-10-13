package jbst.iam.domain.db;

import jbst.foundation.domain.base.Username;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public record JbstSettings(
        Username createdBy,
        long createdAt,
        Username updatedBy,
        long updatedAt,
        JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static JbstSettings hardcoded() {
        return new JbstSettings(
                Username.ops(),
                getCurrentTimestamp(),
                Username.ops(),
                getCurrentTimestamp(),
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
