package jbst.foundation.configurations;

import feign.Feign;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utils.GeoCountryFlagUtils;
import jbst.foundation.utils.GeoLocationMindMaxUtils;
import jbst.foundation.utils.IPAPIGeoLocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ConfigurationUtils {

    // Resources
    private final ResourceLoader resourceLoader;
    // Properties
    private final JbstProperties jbstProperties;

    // TODO [YYL] utilities -> utils
    @PostConstruct
    public void init() {
        this.jbstProperties.getUtilitiesConfigs().assertProperties(new PropertyId("utilitiesConfigs"));
    }

    @Bean
    GeoCountryFlagUtils geoCountryFlagUtils() {
        return new GeoCountryFlagUtils(
                this.resourceLoader,
                this.jbstProperties
        );
    }

    @Bean
    IPAPIGeoLocationUtils ipapiGeoLocationUtils() {
        return new IPAPIGeoLocationUtils(
                Feign.builder()
                        .client(new OkHttpClient())
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .retryer(Retryer.NEVER_RETRY)
                        .target(IPAPIGeoLocationUtils.IPAPIDefinition.class, "http://ip-api.com"),
                this.geoCountryFlagUtils()
        );
    }

    @Bean
    GeoLocationMindMaxUtils geoLocationMindMaxUtils() {
        return new GeoLocationMindMaxUtils(
                this.resourceLoader,
                this.geoCountryFlagUtils(),
                this.jbstProperties
        );
    }
}
