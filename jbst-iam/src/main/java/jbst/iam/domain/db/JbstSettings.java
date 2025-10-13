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

    public static JbstSettings of(
            Username createdBy,
            long createdAt,
            Username updatedBy,
            long updatedAt,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        return new JbstSettings(createdBy, createdAt, updatedBy, updatedAt, hardwareMonitoringThresholds);
    }

    public static JbstSettings hardcoded() {
        return JbstSettings.of(
                Username.ops(),
                getCurrentTimestamp(),
                Username.ops(),
                getCurrentTimestamp(),
                JbstSettingsHardwareMonitoringThresholds.hardcoded()
        );
    }
}
