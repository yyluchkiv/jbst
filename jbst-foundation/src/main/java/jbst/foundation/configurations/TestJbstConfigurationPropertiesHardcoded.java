package jbst.foundation.configurations;

import jbst.foundation.domain.annotations.DeletionScheduled;
import jbst.foundation.domain.properties.JbstProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@DeletionScheduled(version = "1.23")
@Configuration
public class TestJbstConfigurationPropertiesHardcoded {

    @Bean
    public JbstProperties jbstProperties() {
        return JbstProperties.hardcoded();
    }
}
