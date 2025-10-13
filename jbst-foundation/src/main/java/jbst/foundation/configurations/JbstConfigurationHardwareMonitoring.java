package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.hardware.publishers.HardwareMonitoringPublisher;
import jbst.foundation.services.hardware.publishers.impl.HardwareMonitoringPublisherImpl;
import jbst.foundation.services.hardware.resources.HardwareMonitoringResource;
import jbst.foundation.services.hardware.store.HardwareMonitoringStore;
import jbst.foundation.services.hardware.store.impl.HardwareMonitoringStoreImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationHardwareMonitoring {

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    @Bean
    HardwareMonitoringStore hardwareMonitoringStore() {
        return new HardwareMonitoringStoreImpl(
                this.jbstProperties
        );
    }

    @Bean
    HardwareMonitoringPublisher hardwareMonitoringPublisher() {
        return new HardwareMonitoringPublisherImpl(
                this.applicationEventPublisher
        );
    }

    @Bean
    HardwareMonitoringResource hardwareMonitoringResource() {
        return new HardwareMonitoringResource(
                this.hardwareMonitoringPublisher()
        );
    }
}
