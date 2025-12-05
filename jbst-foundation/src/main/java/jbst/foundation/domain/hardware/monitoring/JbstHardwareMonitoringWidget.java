package jbst.foundation.domain.hardware.monitoring;

import jbst.foundation.domain.base.Version;

public record JbstHardwareMonitoringWidget(
        Version version,
        JbstHardwareMonitoringDatapointTableView datapoint
) {

}
