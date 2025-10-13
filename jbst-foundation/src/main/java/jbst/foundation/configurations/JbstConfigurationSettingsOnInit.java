package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstSettingsOnInit.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationSettingsOnInit {

    private final JbstSettingsOnInit jbstSettingsOnInit;

    @PostConstruct
    public void init() {
        this.jbstSettingsOnInit.getHardwareMonitoringThresholds().assertProperties(new PropertyId("hardwareMonitoringThresholds"));
    }
}
