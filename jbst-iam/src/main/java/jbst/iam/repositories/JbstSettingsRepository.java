package jbst.iam.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import jbst.iam.domain.db.JbstSettings;

public interface JbstSettingsRepository {
    JbstSettings saveAs(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    );
}
