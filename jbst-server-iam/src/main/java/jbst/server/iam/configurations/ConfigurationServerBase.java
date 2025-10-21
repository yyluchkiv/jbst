package jbst.server.iam.configurations;

import jakarta.annotation.PostConstruct;
import jbst.server.iam.base.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        // -------------------------------------------------------------------------------------------------------------
        "jbst.server.iam.base",
        "jbst.server.iam.extension"
        // -------------------------------------------------------------------------------------------------------------
})
@EnableConfigurationProperties({
        ServerProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationServerBase {

    // Properties
    private final ServerProperties serverProperties;

    @PostConstruct
    public void init() {
        this.serverProperties.getServerConfigs().assertProperty();
    }
}
