package jbst.iam.utils.impl;

import jakarta.servlet.http.HttpServletRequest;
import jbst.iam.utils.HttpRequestUtils;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import jbst.foundation.domain.http.cache.CachedBodyHttpServletRequest;
import jbst.foundation.domain.http.cache.CachedPayload;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;

import java.util.stream.Stream;

import static jbst.iam.utils.impl.HttpRequestUtilsImpl.CACHED_PAYLOAD_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

@ExtendWith({ SpringExtension.class })
@TestPropertySource(properties = {
        "server.servlet.contextPath=/api"
})
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class HttpRequestUtilsImplTest {

    private static Stream<Arguments> authenticationLoginEndpointCases() {
        return Stream.of(
                Arguments.of("GET", "/api/jbst/security/authentication/login", false),
                Arguments.of("PUT", "/api/jbst/security/authentication/login", false),
                Arguments.of("POST", "/api/jbst/security/authentication/login", true),
                Arguments.of("GET", "/api/jbst/security/authentication/login1", false),
                Arguments.of("PUT", "/api/jbst/security/authentication/login1", false),
                Arguments.of("POST", "/api/jbst/security/authentication/login1", false)
        );
    }

    private static Stream<Arguments> authenticationRefreshTokenEndpointCases() {
        return Stream.of(
                Arguments.of("GET", "/api/jbst/security/authentication/refreshToken", false),
                Arguments.of("PUT", "/api/jbst/security/authentication/refreshToken", false),
                Arguments.of("POST", "/api/jbst/security/authentication/refreshToken", true),
                Arguments.of("GET", "/api/jbst/security/authentication/refreshToken1", false),
                Arguments.of("PUT", "/api/jbst/security/authentication/refreshToken1", false),
                Arguments.of("POST", "/api/jbst/security/authentication/refreshToken1", false)
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        // Properties
        private final JbstProperties jbstProperties;

        @Bean
        HttpRequestUtils httpRequestUtility() {
            return new HttpRequestUtilsImpl(
                    this.jbstProperties
            );
        }
    }

    private final HttpRequestUtils componentUnderTest;

    @ParameterizedTest
    @MethodSource("authenticationLoginEndpointCases")
    void isCachedEndpointTest(String method, String requestURI, boolean expected) {
        // Arrange
        var cachedRequest = mock(CachedBodyHttpServletRequest.class);
        when(cachedRequest.getMethod()).thenReturn(method);
        when(cachedRequest.getRequestURI()).thenReturn(requestURI);

        // Act
        var actual = this.componentUnderTest.isCachedEndpoint(cachedRequest);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void cachePayloadNoCacheTest() {
        // Arrange
        var cachedRequest = mock(CachedBodyHttpServletRequest.class);

        // Act
        this.componentUnderTest.cachePayload(cachedRequest);

        // Assert
        verify(cachedRequest).getMethod();
        verifyNoMoreInteractions(cachedRequest);
    }

    @Test
    void cachePayloadTest() {
        // Arrange
        var cachedRequest = mock(CachedBodyHttpServletRequest.class);
        when(cachedRequest.getMethod()).thenReturn("POST");
        when(cachedRequest.getCachedPayload()).thenReturn(CachedPayload.hardcoded());
        when(cachedRequest.getRequestURI()).thenReturn("/api/jbst/security/authentication/login");

        // Act
        this.componentUnderTest.cachePayload(cachedRequest);

        // Assert
        verify(cachedRequest).getMethod();
        verify(cachedRequest).getRequestURI();
        verify(cachedRequest).getCachedPayload();
        verify(cachedRequest).setAttribute(CACHED_PAYLOAD_ATTRIBUTE, "{}");
        verifyNoMoreInteractions(cachedRequest);
    }

    @Test
    void getCachedPayloadMissingTest() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var payload = randomString();
        when(request.getAttribute(CACHED_PAYLOAD_ATTRIBUTE)).thenReturn(payload);

        // Act
        var cachedPayload = this.componentUnderTest.getCachedPayload(request);

        // Assert
        assertThat(cachedPayload).isEqualTo(payload);
        verify(request).getAttribute(CACHED_PAYLOAD_ATTRIBUTE);
        verifyNoMoreInteractions(request);
    }

    @Test
    void getCachedPayloadPresentTest() {
        // Arrange
        var request = mock(HttpServletRequest.class);

        // Act
        var cachedPayload = this.componentUnderTest.getCachedPayload(request);

        // Assert
        assertThat(cachedPayload).isNull();
        verify(request).getAttribute(CACHED_PAYLOAD_ATTRIBUTE);
        verifyNoMoreInteractions(request);
    }

    @ParameterizedTest
    @MethodSource("authenticationLoginEndpointCases")
    void isAuthenticationLoginEndpointTest(String method, String requestURI, boolean expected) {
        // Arrange
        var cachedRequest = mock(CachedBodyHttpServletRequest.class);
        when(cachedRequest.getMethod()).thenReturn(method);
        when(cachedRequest.getRequestURI()).thenReturn(requestURI);

        // Act
        var actual = this.componentUnderTest.isAuthenticationLoginEndpoint(cachedRequest);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("authenticationRefreshTokenEndpointCases")
    void isAuthenticationRefreshTokenEndpointTest(String method, String requestURI, boolean expected) {
        // Arrange
        var cachedRequest = mock(CachedBodyHttpServletRequest.class);
        when(cachedRequest.getMethod()).thenReturn(method);
        when(cachedRequest.getRequestURI()).thenReturn(requestURI);

        // Act
        var actual = this.componentUnderTest.isAuthenticationRefreshTokenEndpoint(cachedRequest);

        // Assert
        assertThat(actual).isEqualTo(expected);
    }
}
