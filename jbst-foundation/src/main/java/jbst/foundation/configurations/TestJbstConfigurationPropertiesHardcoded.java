package jbst.foundation.configurations;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import jbst.foundation.domain.properties.JbstProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@JbstDeletionScheduled(version = "1.23")
@Configuration
public class TestJbstConfigurationPropertiesHardcoded {

    @Bean
    public JbstProperties jbstProperties() {
        return JbstProperties.hardcoded();
    }
}
