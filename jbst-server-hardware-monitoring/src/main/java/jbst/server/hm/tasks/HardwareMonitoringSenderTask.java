package jbst.server.hm.tasks;

import jbst.foundation.domain.concurrent.AbstractInfiniteTimerTask;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringMetadata;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.server.hm.client.HardwareMonitoringClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.SECONDS;
import static jbst.server.hm.utilities.HardwareMonitoringUtility.getSystemMemories;

@Slf4j
@Component
public class HardwareMonitoringSenderTask extends AbstractInfiniteTimerTask {

    // Clients
    private final HardwareMonitoringClient hardwareMonitoringClient;
    // Properties
    private final JbstProperties jbstProperties;

    @Autowired
    protected HardwareMonitoringSenderTask(
            HardwareMonitoringClient hardwareMonitoringClient,
            JbstProperties jbstProperties
    ) {
        super(
                new SchedulerConfiguration(0L, 30L, SECONDS)
        );
        this.hardwareMonitoringClient = hardwareMonitoringClient;
        this.jbstProperties = jbstProperties;
        this.start();
    }

    @Override
    public void onTick() {
        try {
            this.hardwareMonitoringClient.sendHardwareMonitoringMetadata(
                    new HardwareMonitoringMetadata(
                            this.jbstProperties.getServerConfigs().getMavenConfigs().getVersion(),
                            getSystemMemories()
                    )
            );
        } catch (RuntimeException ex) {
            // ignore
        }
    }
}
