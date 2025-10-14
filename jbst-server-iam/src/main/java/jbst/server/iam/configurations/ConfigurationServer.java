package jbst.server.iam.configurations;

import jbst.foundation.configurations.AbstractJbstSecurityJwtConfigurer;
import jbst.foundation.configurations.JbstConfigurationSecurityJwt;
import jbst.foundation.configurations.JbstConfigurationSecurityJwtFallbackBases;
import jbst.server.iam.base.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

@Configuration
@Import({
        JbstConfigurationSecurityJwt.class,
        JbstConfigurationSecurityJwtFallbackBases.class,
        ConfigurationServerBase.class
})
@EnableConfigurationProperties({
        ServerProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationServer implements AbstractJbstSecurityJwtConfigurer {

    @Override
    public void configure(WebSecurity web) {
        // no configurations
    }

    @Override
    public void configure(HttpSecurity http) {
        // no configurations
    }
}
