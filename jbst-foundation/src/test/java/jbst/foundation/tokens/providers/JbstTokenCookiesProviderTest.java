package jbst.foundation.tokens.providers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstTokenCookiesProviderTest {

    private static Stream<Arguments> readRequestAccessTokenArgs() {
        return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true)
        );
    }

    private static Stream<Arguments> readRequestRefreshTokenArgs() {
        return Stream.of(
                Arguments.of(true, false),
                Arguments.of(false, true)
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstTokenCookiesProvider tokensCookiesProvider() {
            return new JbstTokenCookiesProvider(
                    this.jbstProperties
            );
        }
    }

    private final JbstProperties jbstProperties;

    private final JbstTokenCookiesProvider componentUnderTest;

    @Test
    void createResponseAccessToken() {
        // Arrange
        var jwtAccessToken = JwtAccessToken.random();
        var response = mock(HttpServletResponse.class);

        var cookies = this.jbstProperties.getSecurity().getCookies();
        var accessToken = this.jbstProperties.getSecurity().getJwt().getAccessToken();
        var maxAge = accessToken.getExpiration().getTimeAmount().toSeconds() - cookies.getJwtAccessTokenCookieCreationLatency().getTimeAmount().toSeconds();

        // Act
        this.componentUnderTest.createResponseAccessToken(jwtAccessToken, response);

        // Assert
        var cookieAC = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieAC.capture());
        var cookie = cookieAC.getValue();
        assertThat(cookie.getName()).isEqualTo(accessToken.getCookieKey());
        assertThat(cookie.getValue()).isEqualTo(jwtAccessToken.value());
        assertThat(cookie.getDomain()).isEqualTo(cookies.getDomain());
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(maxAge);
        verifyNoMoreInteractions(response);
    }

    @Test
    void createResponseRefreshToken() {
        // Arrange
        var refreshAccessToken = JwtRefreshToken.random();
        var response = mock(HttpServletResponse.class);

        var cookies = this.jbstProperties.getSecurity().getCookies();
        var refreshToken = this.jbstProperties.getSecurity().getJwt().getRefreshToken();

        // Act
        this.componentUnderTest.createResponseRefreshToken(refreshAccessToken, response);

        // Assert
        var cookieAC = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieAC.capture());
        var cookie = cookieAC.getValue();
        assertThat(cookie.getName()).isEqualTo(refreshToken.getCookieKey());
        assertThat(cookie.getValue()).isEqualTo(refreshAccessToken.value());
        assertThat(cookie.getDomain()).isEqualTo(cookies.getDomain());
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getMaxAge()).isEqualTo(refreshToken.getExpiration().getTimeAmount().toSeconds());
        verifyNoMoreInteractions(response);
    }

    @Test
    void readCsrfToken() throws JbstCsrfTokenNotFoundException {
        // Arrange
        var csrfConfigs = this.jbstProperties.getSecurity().getWebsockets().getCsrf();
        var cookie = mock(Cookie.class);
        var cookieValue = randomString();
        when(cookie.getName()).thenReturn(csrfConfigs.getTokenKey());
        when(cookie.getValue()).thenReturn(cookieValue);
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // Act
        var actual = this.componentUnderTest.readCsrfToken(request);

        // Assert
        assertThat(actual.getHeaderName()).isEqualTo("csrf-header");
        assertThat(actual.getParameterName()).isEqualTo("_csrf");
        assertThat(actual.getToken()).isEqualTo(cookieValue);
        verify(request).getCookies();
        assertThat(request.getCookies()).hasSize(1);
    }

    @Test
    void readCsrfTokenThrow() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { });

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readCsrfToken(request));

        // Assert
        verify(request).getCookies();
        assertThat(request.getCookies()).isEmpty();
        assertThat(throwable)
                .isInstanceOf(JbstCsrfTokenNotFoundException.class)
                .hasMessageContaining("Csrf token not found");
    }

    @ParameterizedTest
    @MethodSource("readRequestAccessTokenArgs")
    void readRequestAccessToken(boolean rest, boolean websocket) throws JbstAccessTokenNotFoundException {
        // Arrange
        var accessToken = this.jbstProperties.getSecurity().getJwt().getAccessToken();
        var cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(accessToken.getCookieKey());
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // Act
        if (rest) {
            var requestAccessToken = this.componentUnderTest.readRequestAccessToken(request);
            assertThat(requestAccessToken).isNotNull();
        }
        if (websocket) {
            this.componentUnderTest.readRequestAccessTokenOnWebsocketHandshake(request);
        }

        // Assert
        verify(request).getCookies();
        assertThat(request.getCookies()).hasSize(1);
    }

    @Test
    void readRequestAccessTokenThrow() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { });

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestAccessToken(request));

        // Assert
        verify(request).getCookies();
        assertThat(request.getCookies()).isEmpty();
        assertThat(throwable)
                .isInstanceOf(JbstAccessTokenNotFoundException.class)
                .hasMessageContaining("JWT access token not found");
    }

    @ParameterizedTest
    @MethodSource("readRequestRefreshTokenArgs")
    void readRequestRefreshToken(boolean rest, boolean websocket) throws JbstRefreshTokenNotFoundException {
        // Arrange
        var refreshToken = this.jbstProperties.getSecurity().getJwt().getRefreshToken();
        var cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(refreshToken.getCookieKey());
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        // Act
        if (rest) {
            this.componentUnderTest.readRequestRefreshToken(request);
        }
        if (websocket) {
            this.componentUnderTest.readRequestRefreshTokenOnWebsocketHandshake(request);
        }

        // Assert
        verify(request).getCookies();
        assertThat(request.getCookies()).hasSize(1);
    }

    @Test
    void readRequestRefreshTokenThrow() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[] { });

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestRefreshToken(request));

        // Assert
        verify(request).getCookies();
        assertThat(request.getCookies()).isEmpty();
        assertThat(throwable)
                .isInstanceOf(JbstRefreshTokenNotFoundException.class)
                .hasMessageContaining("JWT refresh token not found");
    }

    @Test
    void clearTokens() {
        // Arrange
        var response = mock(HttpServletResponse.class);
        var domain = this.jbstProperties.getSecurity().getCookies().getDomain();
        var accessToken = this.jbstProperties.getSecurity().getJwt().getAccessToken();
        var refreshToken = this.jbstProperties.getSecurity().getJwt().getRefreshToken();

        // Act
        this.componentUnderTest.clearTokens(response);

        // Assert
        var cookiesAC = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(2)).addCookie(cookiesAC.capture());
        var cookies = cookiesAC.getAllValues();
        var cookieKeys = Stream.of(accessToken.getCookieKey(), refreshToken.getCookieKey()).collect(Collectors.toSet());
        cookies.forEach(cookie -> {
            assertThat(cookieKeys).contains(cookie.getName());
            assertThat(domain).isEqualTo(cookie.getDomain());
        });
        verifyNoMoreInteractions(response);
    }
}
