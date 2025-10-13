package jbst.iam.repositories;

import jbst.foundation.domain.base.Username;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.settings.JbstSettingsHardwareMonitoringThresholds;

public interface JbstSettingsRepository {
    JbstSettings getSettings();
    boolean isPresent();
    long count();
    JbstSettings saveAs(
            Username username,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    );
}
