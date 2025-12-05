package jbst.server.hm.client;

import feign.FeignException;
import feign.Headers;
import feign.RequestLine;
import jbst.foundation.domain.enums.JbstStatus;
import jbst.foundation.domain.hardware.monitoring.JbstHardwareMonitoringMetadata;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.time.JbstSchedulerConfiguration;
import jbst.foundation.domain.workers.JbstWorkerFixedInfinity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;
import static jbst.foundation.domain.tuples.TuplePercentage.progressTuplePercentage;
import static jbst.foundation.domain.hardware.JbstHardware.getSystemMemories;

@Slf4j
@Component
public class JbstHardwareMonitoringClient extends JbstWorkerFixedInfinity {

    // State
    private final AtomicLong successes = new AtomicLong(0);
    private final AtomicLong failures = new AtomicLong(0);

    @Autowired
    public JbstHardwareMonitoringClient(HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition, JbstProperties jbstProperties) {
        super(
                new JbstSchedulerConfiguration(0L, 30L, SECONDS)
        );
        this.hardwareMonitoringClientDefinition = hardwareMonitoringClientDefinition;
        this.jbstProperties = jbstProperties;
        this.start();
    }

    // Classes: Definitions
    public interface HardwareMonitoringClientDefinition {
        @RequestLine("POST /api/hardware/monitoring/metadata")
        @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
        void sendHardwareMonitoringMetadata(JbstHardwareMonitoringMetadata hardwareMonitoringMetadata);
    }

    // Definitions
    private final HardwareMonitoringClientDefinition hardwareMonitoringClientDefinition;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void onTick() {
        try {
            var metadata = new JbstHardwareMonitoringMetadata(
                    this.jbstProperties.getApp().getMaven().getVersion(),
                    getSystemMemories()
            );
            var status = JbstStatus.STARTED;
            try {
                this.hardwareMonitoringClientDefinition.sendHardwareMonitoringMetadata(metadata);
                this.successes.incrementAndGet();
                status = JbstStatus.SUCCESS;
            } catch (FeignException ex) {
                this.failures.incrementAndGet();
                status = JbstStatus.FAILURE;
            }
            LOGGER.info(
                    "Hardware monitoring client iteration #{}, status: {}. Success rate: {}%",
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
