package jbst.server.hm.client;

import feign.FeignException;
import feign.Headers;
import feign.RequestLine;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HardwareMonitoringClient {

    private final AtomicLong iterations = new AtomicLong(0);
    private final AtomicLong successes = new AtomicLong(0);
    private final AtomicLong failures = new AtomicLong(0);

    // Classes: Definitions
    public interface HardwareMonitoringClientDefinition {
        @RequestLine("POST /api/hardware/monitoring/metadata")
        @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
        void sendHardwareMonitoringMetadata(HardwareMonitoringMetadata hardwareMonitoringMetadata);
    }

    // Definitions
    private final HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition;

    @Async
    public void sendHardwareMonitoringMetadata(HardwareMonitoringMetadata hardwareMonitoringMetadata) {
        this.iterations.incrementAndGet();
        var status = Status.STARTED;
        try {
            this.hardwareMonitoringClientDefinition.sendHardwareMonitoringMetadata(hardwareMonitoringMetadata);
            this.successes.incrementAndGet();
            status = Status.SUCCESS;
        } catch (FeignException ex) {
            this.failures.incrementAndGet();
            status = Status.FAILURE;
        }
        var percentage = progressTuplePercentage(
                this.successes.get(),
                this.successes.get() + this.failures.get()
        ).percentage();
        LOGGER.info(
                "SEND HARDWARE METADATA #{} — {}. Success Rate: {}%",
                this.iterations,
                status.asANSI(),
                percentage
        );
    }
}
