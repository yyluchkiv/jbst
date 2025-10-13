package jbst.iam.resources.hardware;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.foundation.domain.events.hardware.EventLastHardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapoint;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.iam.tasks.hardware.HardwareBackPressureTimerTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import static jbst.foundation.utilities.hardware.HardwareUtility.getHeapMemory;

// Swagger
@Tag(name = "[jbst] Hardware API")
// Spring
@Slf4j
@RestController
@RequestMapping("/hardware/monitoring")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstHardwareMonitoringResource {

    // TimerTasks
    private final HardwareBackPressureTimerTask hardwareBackPressureTimerTask;
    // Incidents
    private final IncidentPublisher incidentPublisher;

    // State
    private final Deque<EventLastHardwareMonitoringDatapoint> datapoints = new ConcurrentLinkedDeque<>();

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
            if (this.hardwareBackPressureTimerTask.isAnyProblemOrFirstDatapoint()) {
                this.hardwareBackPressureTimerTask.send();
            }
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
        }
    }
}
