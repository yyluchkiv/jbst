package jbst.foundation.utils;

import jbst.foundation.configurations.JbstConfigurationUtils;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.http.requests.IPAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GeoLocationIPAPIUtilsConsoleTest {

    @Configuration
    @Import({
            JbstConfigurationUtils.class,
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

    }

    private final GeoLocationIPAPIUtils componentUnderTest;

    @Disabled
    @Test
    void australiaTest() throws GeoLocationNotFoundException {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocation(new IPAddress("1.1.1.1"));

        // Assert
        LOGGER.info("Australia: {}", geoLocation);
    }

    @Disabled
    @Test
    void localhostTest() throws GeoLocationNotFoundException {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocation(new IPAddress("127.0.0.1"));

        // Assert
        LOGGER.debug("localhost: {}", geoLocation);
    }
}
