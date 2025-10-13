package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;

public record HardwareMonitoringWidget(
        Version version,
        HardwareMonitoringDatapointTableView datapoint
) {

    // TODO [YYL] fix hardware monitoring dependency
    public static HardwareMonitoringWidget of(EventLastHardwareMonitoringDatapoint event, Object configs) {
        return new HardwareMonitoringWidget(
                event.version(),
                event.last().tableView(
                        new HardwareMonitoringThresholds(
                               null
                                // configs.getThresholdsConfigs()
                        )
                )
        );
    }
}
