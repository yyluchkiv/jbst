package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utils.JbstGeoUtils;
import jbst.foundation.utils.UserMetadataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@ComponentScan({
        "jbst.foundation.utils"
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationUtils {

    // Resources
    private final ResourceLoader resourceLoader;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getUtilsConfigs().assertProperties(new PropertyId("utilsConfigs"));
    }

    @Bean
    JbstGeoUtils geoUtils() {
        return new JbstGeoUtils(
                this.resourceLoader,
                this.jbstProperties
        );
    }

    @Bean
    UserMetadataUtils userMetadataUtils() {
        return new UserMetadataUtils(
                this.geoUtils()
        );
    }
}
