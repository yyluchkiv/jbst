package jbst.foundation.services.hardware.store.impl;

import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringWidget;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.hardware.store.HardwareMonitoringStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HardwareMonitoringStoreImpl implements HardwareMonitoringStore {

    // Properties
    private final JbstProperties jbstProperties;

    private final Deque<EventLastHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

    @Override
    public HardwareMonitoringWidget getHardwareMonitoringWidget() {
        return HardwareMonitoringWidget.of(
                this.getLastOrUnknownEvent(),
                // TODO [YYL] fix hardware monitoring dependency
                null
                // this.jbstProperties.getHardwareMonitoringConfigs()
        );
    }

    @Override
    public boolean containsOneElement() {
        return this.datapoints.size() == 1;
    }

    @Override
    public void storeEvent(EventLastHardwareMonitoringDatapoint event) {
        if (this.datapoints.size() >= 120) {
            this.datapoints.pollFirst();
        }
        this.datapoints.offerLast(event);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private EventLastHardwareMonitoringDatapoint getLastOrUnknownEvent() {
        if (!isEmpty(this.datapoints)) {
            return this.datapoints.peekLast();
        } else {
            return EventLastHardwareMonitoringDatapoint.unknownVersionZeroUsage();
        }
    }
}
