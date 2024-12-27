package jbst.foundation.utils;

import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.http.requests.IPAddress;
import jbst.foundation.domain.http.requests.UserAgentHeader;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class UserMetadataUtilsTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        GeoLocationUtils geoLocationUtils() {
            return mock(GeoLocationUtils.class);
        }

        @Bean
        UserAgentDetailsUtils userAgentDetailsUtils() {
            return mock(UserAgentDetailsUtils.class);
        }

        @Bean
        UserMetadataUtils userMetadataUtils() {
            return new UserMetadataUtils(
                    this.geoLocationUtils(),
                    this.userAgentDetailsUtils()
            );
        }
    }

    private final GeoLocationUtils geoLocationUtils;
    private final UserAgentDetailsUtils userAgentDetailsUtils;

    private final UserMetadataUtils componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.geoLocationUtils,
                this.userAgentDetailsUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.geoLocationUtils,
                this.userAgentDetailsUtils
        );
    }

    @Test
    void getUserRequestMetadataProcessed() {
        // Act
        var metadata = this.componentUnderTest.getUserRequestMetadataProcessed(
                IPAddress.localhost(),
                UserAgentHeader.hardcoded()
        );

        // Assert
        assertThat(metadata.getStatus()).isEqualTo(Status.COMPLETED);
        verify(this.geoLocationUtils).getGeoLocation(IPAddress.localhost());
        verify(this.userAgentDetailsUtils).getUserAgentDetails(UserAgentHeader.hardcoded());
    }
}
