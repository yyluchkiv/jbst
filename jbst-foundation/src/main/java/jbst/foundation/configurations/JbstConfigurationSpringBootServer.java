package jbst.foundation.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.resources.system.JbstActuatorResource;
import jbst.foundation.utils.JbstEnvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

// Swagger
@OpenAPIDefinition(
        info = @Info(
                title = "${jbst.server-configs.name}",
                version = "${jbst.server-configs.maven-configs.version}"
        )
)
// Spring
@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationSpringBootServer {

    // Environment
    private final Environment environment;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getServerConfigs().assertProperties(new PropertyId("serverConfigs"));
    }

    @Bean
    public JbstEnvUtils envUtils() {
        return new JbstEnvUtils(
                this.environment
        );
    }

    @Bean
    public JbstActuatorResource baseInfoResource() {
        return new JbstActuatorResource(
                this.envUtils(),
                this.jbstProperties
        );
    }
}
