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
        this.jbstProperties.getMvc().assertProperties();
    }

    @Override
    public void addCorsMappings(@NotNull CorsRegistry corsRegistry) {
        var mvc = this.jbstProperties.getMvc();
        if (mvc.isEnabled()) {
            var cors = mvc.getCors();

            var pathPattern = cors.getPathPattern();
            var corsRegistration = corsRegistry.addMapping(pathPattern);

            var allowedOrigins = cors.getAllowedOrigins();
            if (nonNull(allowedOrigins)) {
                corsRegistration.allowedOrigins(allowedOrigins);
            }

            var allowedMethods = cors.getAllowedMethods();
            if (nonNull(allowedMethods)) {
                corsRegistration.allowedMethods(allowedMethods);
            }

            var allowedHeaders = cors.getAllowedHeaders();
            if (nonNull(allowedHeaders)) {
                corsRegistration.allowedHeaders(allowedHeaders);
            }

            var exposedHeaders = cors.getExposedHeaders();
            if (nonNull(exposedHeaders)) {
                corsRegistration.exposedHeaders(exposedHeaders);
            }

            var allowCredentials = cors.isAllowCredentials();
            corsRegistration.allowCredentials(allowCredentials);
        }
    }
}
