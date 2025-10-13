package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstSettingsOnInit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JbstSettingsOnInit.class
})
public class JbstConfigurationSettingsOnInit {
}
