package jbst.foundation.utils;

import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.geo.GeoLocation;
import jbst.foundation.domain.http.requests.IPAddress;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GeoLocationUtilsTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        GeoLocationIPAPIUtils geoLocationIPAPIUtils() {
            return mock(GeoLocationIPAPIUtils.class);
        }

        @Bean
        GeoLocationMindMaxUtils geoLocationMindMaxUtils() {
            return mock(GeoLocationMindMaxUtils.class);
        }

        @Bean
        GeoLocationUtils geoLocationUtils() {
            return new GeoLocationUtils(
                    this.geoLocationIPAPIUtils(),
                    this.geoLocationMindMaxUtils()
            );
        }
    }

    private final GeoLocationIPAPIUtils geoLocationIPAPIUtils;
    private final GeoLocationMindMaxUtils geoLocationMindMaxUtils;

    private final GeoLocationUtils componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.geoLocationIPAPIUtils,
                this.geoLocationMindMaxUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.geoLocationIPAPIUtils,
                this.geoLocationMindMaxUtils
        );
    }

    @Test
    void getGeoLocationThrowExceptionTest() throws JbstGeoLocationNotFoundException {
        // Arrange
        var ipAddress = IPAddress.random();
        var geoLocation = GeoLocation.random();
        when(this.geoLocationIPAPIUtils.getGeoLocation(ipAddress)).thenThrow(new JbstGeoLocationNotFoundException(randomString()));
        when(this.geoLocationMindMaxUtils.getGeoLocation(ipAddress)).thenReturn(geoLocation);

        // Act
        var actual = this.componentUnderTest.getGeoLocation(ipAddress);

        // Assert
        verify(this.geoLocationIPAPIUtils).getGeoLocation(ipAddress);
        verify(this.geoLocationMindMaxUtils).getGeoLocation(ipAddress);
        assertThat(actual).isEqualTo(geoLocation);
    }

    @Test
    void getGeoLocationTest() throws JbstGeoLocationNotFoundException {
        // Arrange
        var ipAddress = IPAddress.random();
        var geoLocation = GeoLocation.random();
        when(this.geoLocationIPAPIUtils.getGeoLocation(ipAddress)).thenReturn(geoLocation);

        // Act
        var actual = this.componentUnderTest.getGeoLocation(ipAddress);

        // Assert
        verify(this.geoLocationIPAPIUtils).getGeoLocation(ipAddress);
        assertThat(actual).isEqualTo(geoLocation);
    }
}
