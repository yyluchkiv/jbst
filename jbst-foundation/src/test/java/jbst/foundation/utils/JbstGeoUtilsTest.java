package jbst.foundation.utils;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesFixed;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.base.IPAddress;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstGeoUtilsTest {

    private static Stream<Arguments> getFlagEmojiTest() {
        return Stream.of(
                Arguments.of(null, null, JbstConstants.Flags.UNKNOWN),
                Arguments.of("Ukraine", "UA", JbstConstants.Flags.UKRAINE),
                Arguments.of("Portugal", "PT", JbstConstants.Flags.PORTUGAL),
                Arguments.of("United States", "US", JbstConstants.Flags.USA),
                Arguments.of(JbstConstants.Strings.UNKNOWN, JbstConstants.Strings.UNKNOWN, JbstConstants.Flags.UNKNOWN),
                Arguments.of(JbstConstants.Strings.UNDEFINED, JbstConstants.Strings.UNDEFINED, JbstConstants.Flags.UNKNOWN)
        );
    }

    private static Stream<Arguments> getUserAgentDetailsTest() {
        return Stream.of(
                Arguments.of("", "Unknown", "Unknown", "Unknown"),
                Arguments.of(randomString(), "Default Browser", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0", "Firefox", "MacOSX", "Desktop")
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesFixed.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final ResourceLoader resourceLoader;
        private final JbstProperties jbstProperties;

        @Bean
        JbstGeoUtils geoUtils() {
            return new JbstGeoUtils(
                    this.resourceLoader,
                    this.jbstProperties
            );
        }
    }

    private final JbstGeoUtils componentUnderTest;

    @ParameterizedTest
    @MethodSource("getFlagEmojiTest")
    void getFlagEmojiTest(String countryName, String countryCode, String expected) {
        // Act
        var actual1 = this.componentUnderTest.getFlagEmojiByCountryName(countryName);
        var actual2 = this.componentUnderTest.getFlagEmojiByCountryCode(countryCode);

        // Assert
        assertThat(actual1).isEqualTo(expected);
        assertThat(actual2).isEqualTo(expected);
    }

    @Test
    void getUserAgentDetailsExceptionTest() {
        // Arrange
        var userAgentHeader = mock(JbstUserAgentHeader.class);

        // Act
        var userAgentDetails = this.componentUnderTest.getUserAgentDetails(userAgentHeader);

        // Assert
        assertThat(userAgentDetails).isNotNull();
        assertThat(userAgentDetails.getBrowser()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getPlatform()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getDeviceType()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getExceptionDetails()).isEqualTo("");
        assertThat(userAgentDetails.getWhat()).isEqualTo("Unknown, Unknown on Unknown");
    }

    @ParameterizedTest
    @MethodSource("getUserAgentDetailsTest")
    void getUserAgentDetailsTest(String header, String browser, String platform, String deviceType) {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(header);
        var userAgentHeader = new JbstUserAgentHeader(request);

        // Act
        var userAgentDetails = this.componentUnderTest.getUserAgentDetails(userAgentHeader);

        // Assert
        assertThat(userAgentDetails).isNotNull();
        assertThat(userAgentDetails.getBrowser()).isEqualTo(browser);
        assertThat(userAgentDetails.getPlatform()).isEqualTo(platform);
        assertThat(userAgentDetails.getDeviceType()).isEqualTo(deviceType);
        assertThat(userAgentDetails.getExceptionDetails()).isEmpty();
    }

    @Disabled
    @Test
    void australiaTest() throws JbstExceptions.GeoLocationNotFound {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocationIPAPI(new IPAddress("1.1.1.1"));

        // Assert
        LOGGER.info("Australia: {}", geoLocation);
    }

    @Disabled
    @Test
    void localhostTest() throws JbstExceptions.GeoLocationNotFound {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocationIPAPI(new IPAddress("127.0.0.1"));

        // Assert
        LOGGER.debug("localhost: {}", geoLocation);
    }
}
