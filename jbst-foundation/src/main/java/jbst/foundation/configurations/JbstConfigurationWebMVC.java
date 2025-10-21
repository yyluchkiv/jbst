package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static java.util.Objects.nonNull;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@EnableWebMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationWebMVC implements WebMvcConfigurer {

    // Properties
    protected final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getMvcConfigs().assertProperty();
    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry corsRegistry) {
        var mvcConfigs = this.jbstProperties.getMvcConfigs();
        if (mvcConfigs.isEnabled()) {
            var corsConfigs = mvcConfigs.getCorsConfigs();

            var pathPattern = corsConfigs.getPathPattern();
            var corsRegistration = corsRegistry.addMapping(pathPattern);

            var allowedOrigins = corsConfigs.getAllowedOrigins();
            if (nonNull(allowedOrigins)) {
                corsRegistration.allowedOrigins(allowedOrigins);
            }

            var allowedMethods = corsConfigs.getAllowedMethods();
            if (nonNull(allowedMethods)) {
                corsRegistration.allowedMethods(allowedMethods);
            }

            var allowedHeaders = corsConfigs.getAllowedHeaders();
            if (nonNull(allowedHeaders)) {
                corsRegistration.allowedHeaders(allowedHeaders);
            }

            var exposedHeaders = corsConfigs.getExposedHeaders();
            if (nonNull(exposedHeaders)) {
                corsRegistration.exposedHeaders(exposedHeaders);
            }

            var allowCredentials = corsConfigs.isAllowCredentials();
            corsRegistration.allowCredentials(allowCredentials);
        }
    }
}
