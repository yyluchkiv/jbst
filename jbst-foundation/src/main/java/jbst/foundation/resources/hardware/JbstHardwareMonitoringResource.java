package jbst.foundation.resources.hardware;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.time.JbstSchedulerConfiguration;
import jbst.foundation.domain.workers.JbstWorkerFixedInfinity;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.websockets.JbstWebsocketsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import static jbst.foundation.domain.hardware.JbstHardware.getHeapMemory;

// Swagger
@Tag(name = "[jbst] Hardware API")
// Spring
@Slf4j
@RestController
@RequestMapping("/hardware/monitoring")
public class JbstHardwareMonitoringResource extends JbstWorkerFixedInfinity {

    // Sessions
    private final JbstSessionRegistry sessionRegistry;
    // Websockets
    private final JbstWebsocketsService websocketsService;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;
    // Stores
    private final JbstHardwareMonitoringStore jbstHardwareMonitoringStore;

    @Autowired
    public JbstHardwareMonitoringResource(
            JbstSessionRegistry sessionRegistry,
            JbstWebsocketsService websocketsService,
            JbstIncidentsPublisher incidentsPublisher,
            JbstHardwareMonitoringStore jbstHardwareMonitoringStore
    ) {
        super(
                new JbstSchedulerConfiguration(60L, 60L, TimeUnit.SECONDS)
        );
        this.sessionRegistry = sessionRegistry;
        this.websocketsService = websocketsService;
        this.incidentsPublisher = incidentsPublisher;
        this.jbstHardwareMonitoringStore = jbstHardwareMonitoringStore;
    }

    @Override
    public void onTick() {
        try {
            this.send();
        } catch (RuntimeException ex) {
            this.incidentsPublisher.publishThrowable(ex);
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
            this.incidentsPublisher.publishThrowable(ex);
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
