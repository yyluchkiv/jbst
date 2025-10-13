package jbst.iam.domain.db;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public record JbstSettings(
        Username createdBy,
        long createdAt,
        Username updatedBy,
        long updatedAt,
        JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static JbstSettings of(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        return new JbstSettings(
                username,
                getCurrentTimestamp(),
                username,
                getCurrentTimestamp(),
                hardwareMonitoringThresholds
        );
    }
}
