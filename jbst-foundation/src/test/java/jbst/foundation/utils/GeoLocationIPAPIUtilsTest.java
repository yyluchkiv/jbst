package jbst.foundation.utils;

import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
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

import static jbst.foundation.domain.tests.constants.TestsFlagsConstants.UKRAINE;
import static jbst.foundation.utilities.random.RandomUtility.randomFeignException;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class GeoLocationIPAPIUtilsTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        GeoLocationIPAPIUtils.IPAPIDefinition definition() {
            return mock(GeoLocationIPAPIUtils.IPAPIDefinition.class);
        }

        @Bean
        JbstGeoUtils geoUtils() {
            return mock(JbstGeoUtils.class);
        }

        @Bean
        GeoLocationIPAPIUtils geoLocationIPAPIUtils() {
            return new GeoLocationIPAPIUtils(
                    this.definition(),
                    this.geoUtils()
            );
        }
    }

    private final GeoLocationIPAPIUtils.IPAPIDefinition definition;
    private final JbstGeoUtils geoUtils;

    private final GeoLocationIPAPIUtils componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.definition,
                this.geoUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.definition,
                this.geoUtils
        );
    }

    @Test
    void getGeoLocationThrowFeignExceptionTest() {
        // Arrange
        var ipAddress = IPAddress.random();
        var feignException = randomFeignException();
        when(this.definition.getIPAPIResponse(ipAddress.value())).thenThrow(feignException);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.getGeoLocation(ipAddress));

        // Assert
        verify(this.definition).getIPAPIResponse(ipAddress.value());
        assertThat(throwable.getClass()).isEqualTo(JbstGeoLocationNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Geo location not found: " + feignException.getMessage());
    }

    @Test
    void getGeoLocationAPIFailureTest() {
        // Arrange
        var ipAddress = IPAddress.random();
        var ipapiResponse = new GeoLocationIPAPIUtils.IPAPIResponse("fail", null, null, null, "reserved range");
        when(this.definition.getIPAPIResponse(ipAddress.value())).thenReturn(ipapiResponse);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.getGeoLocation(ipAddress));

        // Assert
        verify(this.definition).getIPAPIResponse(ipAddress.value());
        assertThat(throwable.getClass()).isEqualTo(JbstGeoLocationNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Geo location not found: reserved range");
    }

    @Test
    void getGeoLocationTest() throws JbstGeoLocationNotFoundException {
        // Arrange
        var ipAddress = IPAddress.random();
        var ipapiResponse = new GeoLocationIPAPIUtils.IPAPIResponse("success", "Ukraine", "UA", "Lviv", null);
        when(this.definition.getIPAPIResponse(ipAddress.value())).thenReturn(ipapiResponse);
        when(this.geoUtils.getFlagEmojiByCountryCode("UA")).thenReturn(UKRAINE);

        // Act
        var actual = this.componentUnderTest.getGeoLocation(ipAddress);

        // Assert
        verify(this.definition).getIPAPIResponse(ipAddress.value());
        verify(this.geoUtils).getFlagEmojiByCountryCode("UA");
        assertThat(actual.getIpAddr()).isEqualTo(ipAddress.value());
        assertThat(actual.getCountry()).isEqualTo("Ukraine");
        assertThat(actual.getCountryCode()).isEqualTo("UA");
        assertThat(actual.getCountryFlag()).isEqualTo(UKRAINE);
        assertThat(actual.getCity()).isEqualTo("Lviv");
    }
}
