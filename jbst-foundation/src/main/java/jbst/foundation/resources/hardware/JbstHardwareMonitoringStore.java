package jbst.foundation.resources.hardware;

import jbst.foundation.domain.annotations.JbstDevelopmentOnly;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringWidget;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstHardwareMonitoringStore {

    // Settings
    private final JbstSettingsService settingsService;

    private final Deque<JbstHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

    @JbstDevelopmentOnly
    public final void clear() {
        this.datapoints.clear();
    }

    public final JbstHardwareMonitoringWidget getWidget() {
        var datapoint = !this.datapoints.isEmpty() ? this.datapoints.peekLast() : JbstHardwareMonitoringDatapoint.zeroUsage();
        return datapoint.getWidget(this.settingsService.getHardwareMonitoringThresholds().values());
    }

    public final boolean isAnyProblemOrFirstDatapoint() {
        return this.containsOneElement() || this.getWidget().datapoint().isAnyProblem();
    }

    public final boolean containsOneElement() {
        return this.datapoints.size() == 1;
    }

    public final void storeDatapoint(JbstHardwareMonitoringDatapoint datapoint) {
        if (this.datapoints.size() >= 120) {
            this.datapoints.pollFirst();
        }
        this.datapoints.offerLast(datapoint);
    }
}
