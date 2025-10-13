package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
public class JbstConfigurationProperties {
}
