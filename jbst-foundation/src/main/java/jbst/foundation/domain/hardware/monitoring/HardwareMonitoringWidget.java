package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;

import java.math.BigDecimal;
import java.util.Map;

public record HardwareMonitoringWidget(
        Version version,
        HardwareMonitoringDatapointTableView datapoint
) {

    // TODO [YYL] fix hardware monitoring dependency
    public static HardwareMonitoringWidget of(HardwareMonitoringDatapoint datapoint, Map<HardwareName, BigDecimal> thresholds) {
        return new HardwareMonitoringWidget(
                datapoint.getVersion(),
                datapoint.tableView(
                        new HardwareMonitoringThresholds(
                                thresholds
                        )
                )
        );
    }
}
