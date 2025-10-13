package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;

import java.math.BigDecimal;
import java.util.Map;

public record HardwareMonitoringWidget(
        Version version,
        HardwareMonitoringDatapointTableView datapoint
) {

    // TODO [YYL] fix hardware monitoring dependency
    public static HardwareMonitoringWidget of(EventLastHardwareMonitoringDatapoint event, Map<HardwareName, BigDecimal> thresholds) {
        return new HardwareMonitoringWidget(
                event.version(),
                event.last().tableView(
                        new HardwareMonitoringThresholds(
                                thresholds
                        )
                )
        );
    }
}
