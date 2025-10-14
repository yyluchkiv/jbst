package jbst.iam.domain.db;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.settings.JbstSettingsHardwareMonitoringThresholds;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static java.time.ZoneOffset.UTC;
import static jbst.foundation.domain.constants.JbstConstants.DateTimeFormatters.DTF12;
import static jbst.foundation.utilities.time.LocalDateTimeUtility.convertTimestamp;
import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

@Getter
@EqualsAndHashCode
@ToString
public class JbstSettings {
    private final Username createdBy;
    private final String createdAt;
    private final Username updatedBy;
    private final String updatedAt;
    private final JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds;

    public JbstSettings(
            Username createdBy,
            long createdAt,
            Username updatedBy,
            long updatedAt,
            JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds
    ) {
        this.createdBy = createdBy;
        this.createdAt = DTF12.format(convertTimestamp(createdAt, UTC));
        this.updatedBy = updatedBy;
        this.updatedAt = DTF12.format(convertTimestamp(updatedAt, UTC));
        this.hardwareMonitoringThresholds = hardwareMonitoringThresholds;
    }

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
