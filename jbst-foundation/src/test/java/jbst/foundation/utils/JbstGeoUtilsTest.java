package jbst.foundation.utils;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.geo.JbstGeoLocationNotFoundException;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.tests.constants.TestsFlagsConstants;
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

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstGeoUtilsTest {

    private static Stream<Arguments> getFlagEmojiTest() {
        return Stream.of(
                Arguments.of(null, null, TestsFlagsConstants.UNKNOWN),
                Arguments.of("Ukraine", "UA", TestsFlagsConstants.UKRAINE),
                Arguments.of("Portugal", "PT", TestsFlagsConstants.PORTUGAL),
                Arguments.of("United States", "US", TestsFlagsConstants.USA),
                Arguments.of(JbstConstants.Strings.UNKNOWN, JbstConstants.Strings.UNKNOWN, TestsFlagsConstants.UNKNOWN),
                Arguments.of(JbstConstants.Strings.UNDEFINED, JbstConstants.Strings.UNDEFINED, TestsFlagsConstants.UNKNOWN)
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
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

    @Disabled
    @Test
    void australiaTest() throws JbstGeoLocationNotFoundException {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocationIPAPI(new IPAddress("1.1.1.1"));

        // Assert
        LOGGER.info("Australia: {}", geoLocation);
    }

    @Disabled
    @Test
    void localhostTest() throws JbstGeoLocationNotFoundException {
        // Act
        var geoLocation = this.componentUnderTest.getGeoLocationIPAPI(new IPAddress("127.0.0.1"));

        // Assert
        LOGGER.debug("localhost: {}", geoLocation);
    }
}
