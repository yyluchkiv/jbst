package jbst.iam.resources.hardware;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.concurrent.AbstractInfiniteTimerTask;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringWidget;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.iam.sessions.SessionRegistry;
import jbst.iam.settings.AbstractJbstSettingsService;
import jbst.iam.template.WssMessagingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.hardware.HardwareUtility.getHeapMemory;
import static org.springframework.util.CollectionUtils.isEmpty;

// Swagger
@Tag(name = "[jbst] Hardware API")
// Spring
@Slf4j
@RestController
@RequestMapping("/hardware/monitoring")
public class JbstHardwareMonitoringResource extends AbstractInfiniteTimerTask {

    // Settings
    private final AbstractJbstSettingsService jbstSettingsService;
    // Sessions
    private final SessionRegistry sessionRegistry;
    // Websockets
    private final WssMessagingTemplate wssMessagingTemplate;
    // Publishers
    private final IncidentPublisher incidentPublisher;

    // State
    private final Deque<EventLastHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

    // 60L seconds -> consider add to user settings but on 25.11.2022 no reason to add
    @Autowired
    public JbstHardwareMonitoringResource(
            AbstractJbstSettingsService jbstSettingsService,
            SessionRegistry sessionRegistry,
            WssMessagingTemplate wssMessagingTemplate,
            IncidentPublisher incidentPublisher
    ) {
        super(
                new SchedulerConfiguration(60L, 60L, TimeUnit.SECONDS)
        );
        this.jbstSettingsService = jbstSettingsService;
        this.sessionRegistry = sessionRegistry;
        this.wssMessagingTemplate = wssMessagingTemplate;
        this.incidentPublisher = incidentPublisher;
    }

    @Override
    public void onTick() {
        try {
            this.send();
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    @PostMapping("/metadata")
    @ResponseStatus(HttpStatus.OK)
    public void saveMetadata(@RequestBody HardwareMonitoringMetadata hardwareMonitoringMetadata) {
        try {
            var event = new EventLastHardwareMonitoringDatapoint(
                    hardwareMonitoringMetadata.version(),
                    new HardwareMonitoringDatapoint(
                            hardwareMonitoringMetadata.systemMemories().global(),
                            hardwareMonitoringMetadata.systemMemories().cpu(),
                            getHeapMemory()
                    )
            );
            if (this.datapoints.size() >= 120) {
                this.datapoints.pollFirst();
            }
            this.datapoints.offerLast(event);
            if (this.isAnyProblemOrFirstDatapoint()) {
                this.send();
            }
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void send() {
        this.wssMessagingTemplate.sendHardwareMonitoring(
                this.sessionRegistry.getActiveSessionsUsernames(),
                this.getWidget().datapoint()
        );
    }

    private boolean isAnyProblemOrFirstDatapoint() {
        return this.datapoints.size() == 1 || this.getWidget().datapoint().isAnyProblem();
    }

    private EventLastHardwareMonitoringDatapoint getLastOrUnknownEvent() {
        if (!isEmpty(this.datapoints)) {
            return this.datapoints.peekLast();
        } else {
            return EventLastHardwareMonitoringDatapoint.unknownVersionZeroUsage();
        }
    }

    private HardwareMonitoringWidget getWidget() {
        return HardwareMonitoringWidget.of(
                this.getLastOrUnknownEvent(),
                this.jbstSettingsService.getSettings().hardwareMonitoringThresholds().getValues()
        );
    }
}
