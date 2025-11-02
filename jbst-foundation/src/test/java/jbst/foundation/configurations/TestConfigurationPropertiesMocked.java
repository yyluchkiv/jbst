package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.JbstPropertyMVC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class TestConfigurationPropertiesMocked {

    @Bean
    JbstProperties jbstProperties() {
        var jbstProperties = mock(JbstProperties.class);
        when(jbstProperties.getMvcConfigs()).thenReturn(JbstPropertyMVC.hardcoded());
        return jbstProperties;
    }
}
