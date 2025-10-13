package jbst.iam.resources.hardware;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.concurrent.AbstractInfiniteTimerTask;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.iam.sessions.SessionRegistry;
import jbst.iam.template.WssMessagingTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static jbst.foundation.utilities.hardware.HardwareUtility.getHeapMemory;

// Swagger
@Tag(name = "[jbst] Hardware API")
// Spring
@Slf4j
@RestController
@RequestMapping("/hardware/monitoring")
public class JbstHardwareMonitoringResource extends AbstractInfiniteTimerTask {

    // Sessions
    private final SessionRegistry sessionRegistry;
    // Websockets
    private final WssMessagingTemplate wssMessagingTemplate;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // State
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    // State
//    protected final Deque<EventLastHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

    // 60L seconds -> consider add to user settings but on 25.11.2022 no reason to add
    @Autowired
    public JbstHardwareMonitoringResource(
            SessionRegistry sessionRegistry,
            WssMessagingTemplate wssMessagingTemplate,
            IncidentPublisher incidentPublisher,
            JbstHardwareMonitoringStore jbstHardwareMonitoringStore
    ) {
        super(
                new SchedulerConfiguration(60L, 60L, TimeUnit.SECONDS)
        );
        this.sessionRegistry = sessionRegistry;
        this.wssMessagingTemplate = wssMessagingTemplate;
        this.incidentPublisher = incidentPublisher;
        this.jbstHardwareMonitoringStore = jbstHardwareMonitoringStore;
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
            this.jbstHardwareMonitoringStore.storeEvent(
                    new EventLastHardwareMonitoringDatapoint(
                            hardwareMonitoringMetadata.version(),
                            new HardwareMonitoringDatapoint(
                                    hardwareMonitoringMetadata.systemMemories().global(),
                                    hardwareMonitoringMetadata.systemMemories().cpu(),
                                    getHeapMemory()
                            )
                    )
            );
            if (this.jbstHardwareMonitoringStore.isAnyProblemOrFirstDatapoint()) {
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
                this.jbstHardwareMonitoringStore.getWidget().datapoint()
        );
    }
}
