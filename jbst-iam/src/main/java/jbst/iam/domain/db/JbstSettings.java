package jbst.iam.domain.db;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

public record JbstSettings(
        Username createdBy,
        long createdAt,
        Username updatedBy,
        long updatedAt,
        JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
) {

    public static JbstSettings hardcoded() {
        var settingsOnInit = JbstSettingsOnInit.hardcoded();
        return new JbstSettings(
                Username.ops(),
                getCurrentTimestamp(),
                Username.ops(),
                getCurrentTimestamp(),
                settingsOnInit.getHardwareMonitoringThresholds()
        );
    }

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
