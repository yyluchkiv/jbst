package jbst.server.hm.client;

import feign.FeignException;
import feign.Headers;
import feign.RequestLine;
import jbst.foundation.domain.concurrent.AbstractInfiniteTimerTask;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.time.SchedulerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;
import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;
import static jbst.server.hm.utilities.HardwareMonitoringUtility.getSystemMemories;

@Slf4j
@Component
public class HardwareMonitoringClient extends AbstractInfiniteTimerTask {

    private final AtomicLong successes = new AtomicLong(0);
    private final AtomicLong failures = new AtomicLong(0);

    @Autowired
    public HardwareMonitoringClient(
            HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition,
            JbstProperties jbstProperties
    ) {
        super(
                new SchedulerConfiguration(0L, 30L, SECONDS)
        );
        this.hardwareMonitoringClientDefinition = hardwareMonitoringClientDefinition;
        this.jbstProperties = jbstProperties;
        this.start();
    }

    // Classes: Definitions
    public interface HardwareMonitoringClientDefinition {
        @RequestLine("POST /api/hardware/monitoring/metadata")
        @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
        void sendHardwareMonitoringMetadata(HardwareMonitoringMetadata hardwareMonitoringMetadata);
    }

    // Definitions
    private final HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void onTick() {
        try {
            var metadata = new HardwareMonitoringMetadata(
                    this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion(),
                    getSystemMemories()
            );
            var status = Status.STARTED;
            try {
                this.hardwareMonitoringClientDefinition.sendHardwareMonitoringMetadata(metadata);
                this.successes.incrementAndGet();
                status = Status.SUCCESS;
            } catch (FeignException ex) {
                this.failures.incrementAndGet();
                status = Status.FAILURE;
            }
            LOGGER.info(
                    "SEND HARDWARE METADATA #{} — {}. Success Rate: {}%",
                    this.successes.get() + this.failures.get(),
                    status.asANSI(),
                    progressTuplePercentage(
                            this.successes.get(),
                            this.successes.get() + this.failures.get()
                    ).percentage()
            );
        } catch (RuntimeException ex) {
            // ignore
        }
    }
}
