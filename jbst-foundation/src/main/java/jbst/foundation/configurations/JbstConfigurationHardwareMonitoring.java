package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.hardware.store.HardwareMonitoringStore;
import jbst.foundation.services.hardware.store.impl.HardwareMonitoringStoreImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationHardwareMonitoring {

    // Properties
    private final JbstProperties jbstProperties;

    @Bean
    HardwareMonitoringStore hardwareMonitoringStore() {
        return new HardwareMonitoringStoreImpl(
                this.jbstProperties
        );
    }
}
