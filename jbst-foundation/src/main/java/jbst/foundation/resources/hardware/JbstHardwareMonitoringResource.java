package jbst.foundation.resources.hardware;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.concurrent.AbstractInfiniteTimerTask;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.websockets.WebsocketsService;
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
    private final JbstSessionRegistry sessionRegistry;
    // Websockets
    private final WebsocketsService websocketsService;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Stores
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    @Autowired
    public JbstHardwareMonitoringResource(
            JbstSessionRegistry sessionRegistry,
            WebsocketsService websocketsService,
            IncidentPublisher incidentPublisher,
            JbstHardwareMonitoringStore jbstHardwareMonitoringStore
    ) {
        super(
                new SchedulerConfiguration(60L, 60L, TimeUnit.SECONDS)
        );
        this.sessionRegistry = sessionRegistry;
        this.websocketsService = websocketsService;
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
            this.jbstHardwareMonitoringStore.storeDatapoint(
                    new HardwareMonitoringDatapoint(
                            hardwareMonitoringMetadata.version(),
                            hardwareMonitoringMetadata.systemMemories().global(),
                            hardwareMonitoringMetadata.systemMemories().cpu(),
                            getHeapMemory()
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
        this.websocketsService.sendHardwareMonitoring(
                this.sessionRegistry.getActiveSessionsUsernames(),
                this.jbstHardwareMonitoringStore.getWidget().datapoint()
        );
    }
}
