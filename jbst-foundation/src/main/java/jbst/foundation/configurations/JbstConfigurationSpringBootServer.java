package jbst.foundation.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.resources.system.JbstActuatorResource;
import jbst.foundation.utils.JbstEnvUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.EnumFeature;

// Swagger
@OpenAPIDefinition(
        info = @Info(
                title = "${jbst.app.name}",
                version = "${jbst.app.maven.version}"
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
        this.jbstProperties.getApp().assertProperties();
    }

    /**
     * Keep the Jackson 2 wire format on the Spring-managed mapper (declaration-order properties,
     * name()-based enums) — see {@link jbst.foundation.domain.jsons.JbstObjectMappers}.
     */
    @Bean
    public JsonMapperBuilderCustomizer jbstJackson2CompatibilityCustomizer() {
        return builder -> builder
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING, EnumFeature.WRITE_ENUMS_USING_TO_STRING);
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
