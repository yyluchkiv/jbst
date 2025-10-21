package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "jbst.foundation.assistants.current",
        "jbst.foundation.tasks"
        // -------------------------------------------------------------------------------------------------------------
})
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationSecurityJwtFallbacks {

}
