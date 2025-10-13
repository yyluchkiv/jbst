package jbst.foundation.domain.properties;

import jbst.foundation.domain.annotations.DeletionScheduled;
import jbst.foundation.domain.properties.settings.JbstSettingsHardwareMonitoringThresholds;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@DeletionScheduled(version = "1.23")
@Slf4j
@ConfigurationProperties(
        prefix = "jbst-settings-on-init",
        ignoreUnknownFields = false
)
@Data
public class JbstSettingsOnInit implements PriorityOrdered {
    private JbstSettingsHardwareMonitoringThresholds hardwareMonitoringThresholds;

    public static JbstSettingsOnInit hardcoded() {
        var settings = new JbstSettingsOnInit();
        settings.setHardwareMonitoringThresholds(JbstSettingsHardwareMonitoringThresholds.hardcoded());
        return settings;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
