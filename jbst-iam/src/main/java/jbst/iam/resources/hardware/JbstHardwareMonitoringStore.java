package jbst.iam.resources.hardware;

import jbst.foundation.domain.annotations.DevelopmentOnly;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringWidget;
import jbst.iam.settings.AbstractJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstHardwareMonitoringStore {

    // Settings
    private final AbstractJbstSettingsService jbstSettingsService;

    private final Deque<EventLastHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

    @DevelopmentOnly
    public final void clear() {
        this.datapoints.clear();
    }

    public final HardwareMonitoringWidget getWidget() {
        return HardwareMonitoringWidget.of(
                !this.datapoints.isEmpty() ? this.datapoints.peekLast() : EventLastHardwareMonitoringDatapoint.unknownVersionZeroUsage(),
                this.jbstSettingsService.getSettings().hardwareMonitoringThresholds().getValues()
        );
    }

    public final boolean isAnyProblemOrFirstDatapoint() {
        return this.containsOneElement() || this.getWidget().datapoint().isAnyProblem();
    }

    public final boolean containsOneElement() {
        return this.datapoints.size() == 1;
    }

    public final void storeEvent(EventLastHardwareMonitoringDatapoint event) {
        if (this.datapoints.size() >= 120) {
            this.datapoints.pollFirst();
        }
        this.datapoints.offerLast(event);
    }
}
