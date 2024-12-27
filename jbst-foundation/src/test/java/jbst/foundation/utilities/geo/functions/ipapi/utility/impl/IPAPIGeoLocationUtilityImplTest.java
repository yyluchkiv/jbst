package jbst.foundation.utilities.geo.functions.ipapi.utility.impl;

import jbst.foundation.domain.exceptions.geo.GeoLocationNotFoundException;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.utilities.geo.facades.GeoCountryFlagUtility;
import jbst.foundation.utilities.geo.functions.ipapi.domain.IPAPIResponse;
import jbst.foundation.utilities.geo.functions.ipapi.feign.IPAPIFeign;
import jbst.foundation.utilities.geo.functions.ipapi.utility.IPAPIGeoLocationUtility;
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

@Deprecated(forRemoval = true)
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class IPAPIGeoLocationUtilityImplTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        IPAPIFeign ipapiFeign() {
            return mock(IPAPIFeign.class);
        }

        @Bean
        GeoCountryFlagUtility countryFlagUtility() {
            return mock(GeoCountryFlagUtility.class);
        }

        @Bean
        IPAPIGeoLocationUtility ipapiGeoLocationUtility() {
            return new IPAPIGeoLocationUtilityImpl(
                    this.ipapiFeign(),
                    this.countryFlagUtility()
            );
        }
    }

    private final IPAPIFeign ipapiFeign;
    private final GeoCountryFlagUtility countryFlagUtility;

    private final IPAPIGeoLocationUtility componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.ipapiFeign,
                this.countryFlagUtility
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.ipapiFeign,
                this.countryFlagUtility
        );
    }

    @Test
    void getGeoLocationThrowFeignExceptionTest() {
        // Arrange
        var ipAddress = IPAddress.random();
        var feignException = randomFeignException();
        when(this.ipapiFeign.getIPAPIResponse(ipAddress.value())).thenThrow(feignException);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.getGeoLocation(ipAddress));

        // Assert
        verify(this.ipapiFeign).getIPAPIResponse(ipAddress.value());
        assertThat(throwable.getClass()).isEqualTo(GeoLocationNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Geo location not found: " + feignException.getMessage());
    }

    @Test
    void getGeoLocationAPIFailureTest() {
        // Arrange
        var ipAddress = IPAddress.random();
        var ipapiResponse = new IPAPIResponse("fail", null, null, null, "reserved range");
        when(this.ipapiFeign.getIPAPIResponse(ipAddress.value())).thenReturn(ipapiResponse);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.getGeoLocation(ipAddress));

        // Assert
        verify(this.ipapiFeign).getIPAPIResponse(ipAddress.value());
        assertThat(throwable.getClass()).isEqualTo(GeoLocationNotFoundException.class);
        assertThat(throwable.getMessage()).isEqualTo("Geo location not found: reserved range");
    }

    @Test
    void getGeoLocationTest() throws GeoLocationNotFoundException {
        // Arrange
        var ipAddress = IPAddress.random();
        var ipapiResponse = new IPAPIResponse("success", "Ukraine", "UA", "Lviv", null);
        when(this.ipapiFeign.getIPAPIResponse(ipAddress.value())).thenReturn(ipapiResponse);
        when(this.countryFlagUtility.getFlagEmojiByCountryCode("UA")).thenReturn(UKRAINE);

        // Act
        var actual = this.componentUnderTest.getGeoLocation(ipAddress);

        // Assert
        verify(this.ipapiFeign).getIPAPIResponse(ipAddress.value());
        verify(this.countryFlagUtility).getFlagEmojiByCountryCode("UA");
        assertThat(actual.getIpAddr()).isEqualTo(ipAddress.value());
        assertThat(actual.getCountry()).isEqualTo("Ukraine");
        assertThat(actual.getCountryCode()).isEqualTo("UA");
        assertThat(actual.getCountryFlag()).isEqualTo(UKRAINE);
        assertThat(actual.getCity()).isEqualTo("Lviv");
    }
}
