package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;

public record HardwareMonitoringWidget(
        Version version,
        HardwareMonitoringDatapointTableView datapoint
) {

}
