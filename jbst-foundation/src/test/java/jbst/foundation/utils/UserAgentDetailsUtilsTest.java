package jbst.foundation.utils;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.configurations.TestConfigurationPropertiesJbstHardcoded;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.reflections.ReflectionUtility.setPrivateField;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserAgentDetailsUtilsTest {

    private static Stream<Arguments> getUserAgentDetailsTest() {
        return Stream.of(
                Arguments.of("", "Unknown", "Unknown", "Unknown"),
                Arguments.of(randomString(), "Default Browser", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Macintosh; Intel Mac OS X x.y; rv:42.0) Gecko/20100101 Firefox/42.0", "Firefox", "MacOSX", "Desktop")
        );
    }

    @Configuration
    @Import({
            TestConfigurationPropertiesJbstHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        UserAgentDetailsUtils userAgentDetailsUtils() {
            return new UserAgentDetailsUtils(
                    this.jbstProperties
            );
        }
    }

    private final UserAgentDetailsUtils componentUnderTest;

    @Test
    void getUserAgentDetailsExceptionTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var userAgentHeader = mock(UserAgentHeader.class);
        setPrivateField(this.componentUnderTest, "configured", false);
        setPrivateField(this.componentUnderTest, "exceptionMessage", "CPU spike");

        // Act
        var userAgentDetails = this.componentUnderTest.getUserAgentDetails(userAgentHeader);

        // Assert
        assertThat(userAgentDetails).isNotNull();
        assertThat(userAgentDetails.getBrowser()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getPlatform()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getDeviceType()).isEqualTo("Unknown");
        assertThat(userAgentDetails.getExceptionDetails()).isEqualTo("CPU spike");
        assertThat(userAgentDetails.getWhat()).isEqualTo("Unknown, Unknown on Unknown");
    }

    @ParameterizedTest
    @MethodSource("getUserAgentDetailsTest")
    void getUserAgentDetailsTest(String header, String browser, String platform, String deviceType) {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("User-Agent")).thenReturn(header);
        var userAgentHeader = new UserAgentHeader(request);

        // Act
        var userAgentDetails = this.componentUnderTest.getUserAgentDetails(userAgentHeader);

        // Assert
        assertThat(userAgentDetails).isNotNull();
        assertThat(userAgentDetails.getBrowser()).isEqualTo(browser);
        assertThat(userAgentDetails.getPlatform()).isEqualTo(platform);
        assertThat(userAgentDetails.getDeviceType()).isEqualTo(deviceType);
        assertThat(userAgentDetails.getExceptionDetails()).isEmpty();
    }
}
