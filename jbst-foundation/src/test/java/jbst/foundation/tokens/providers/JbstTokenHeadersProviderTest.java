package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.configs.security.JbstPropertySecurityJWT;
import jbst.foundation.domain.random.JbstRandom;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static jbst.foundation.domain.random.JbstRandom.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstTokenHeadersProviderTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstTokenHeadersProvider tokenHeadersProvider() {
            return new JbstTokenHeadersProvider(
                    this.jbstProperties
            );
        }
    }

    private final JbstProperties jbstProperties;

    private final JbstTokenHeadersProvider componentUnderTest;

    @Test
    void createResponseAccessToken() {
        // Arrange
        var jwtAccessToken = JwtAccessToken.random();
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.createResponseAccessToken(jwtAccessToken, response);

        // Assert
        verify(response).addHeader(this.jwt().getAccessToken().getHeaderKey(), jwtAccessToken.value());
        verifyNoMoreInteractions(response);
    }

    @Test
    void createResponseRefreshToken() {
        // Arrange
        var refreshAccessToken = JwtRefreshToken.random();
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.createResponseRefreshToken(refreshAccessToken, response);

        // Assert
        verify(response).addHeader(this.jwt().getRefreshToken().getHeaderKey(), refreshAccessToken.value());
        verifyNoMoreInteractions(response);
    }

    @Test
    void readCsrfToken() throws JbstExceptions.CsrfTokenNotFound {
        // Arrange
        var csrfConfigs = this.jbstProperties.getSecurity().getWebsockets().getCsrf();
        var header = randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getParameter(csrfConfigs.getTokenKey())).thenReturn(header);

        // Act
        var actual = this.componentUnderTest.readCsrfToken(request);

        // Assert
        assertThat(actual.getHeaderName()).isEqualTo("csrf-header");
        assertThat(actual.getParameterName()).isEqualTo("_csrf");
        assertThat(actual.getToken()).isEqualTo(header);
        verify(request).getParameter(csrfConfigs.getTokenKey());
    }

    @Test
    void readCsrfTokenThrow() {
        // Arrange
        var request = mock(HttpServletRequest.class);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readCsrfToken(request));

        // Assert
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.CsrfTokenNotFound.class)
                .hasMessageContaining("Csrf token not found");
    }

    @Test
    void readRequestAccessToken() throws JbstExceptions.AccessTokenNotFound {
        // Arrange
        var headerKey = this.jwt().getAccessToken().getHeaderKey();
        var header = JbstRandom.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(headerKey)).thenReturn(header);

        // Act
        this.componentUnderTest.readRequestAccessToken(request);

        // Assert
        verify(request).getHeader(headerKey);
        assertThat(request.getHeader(headerKey)).isEqualTo(header);
    }

    @Test
    void readRequestAccessTokenThrow() {
        // Arrange
        var headerKey = this.jwt().getAccessToken().getHeaderKey();
        var request = mock(HttpServletRequest.class);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestAccessToken(request));

        // Assert
        verify(request).getHeader(headerKey);
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.AccessTokenNotFound.class)
                .hasMessageContaining("JWT access token not found");
    }

    @Test
    void readRequestAccessTokenOnWebsocketHandshake() throws JbstExceptions.AccessTokenNotFound {
        // Arrange
        var headerKey = this.jwt().getAccessToken().getHeaderKey();
        var header = JbstRandom.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getParameter(headerKey)).thenReturn(header);

        // Act
        this.componentUnderTest.readRequestAccessTokenOnWebsocketHandshake(request);

        // Assert
        verify(request).getParameter(headerKey);
        assertThat(request.getParameter(headerKey)).isEqualTo(header);
    }

    @Test
    void readRequestAccessTokenOnWebsocketHandshakeThrow() {
        // Arrange
        var headerKey = this.jwt().getAccessToken().getHeaderKey();
        var request = mock(HttpServletRequest.class);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestAccessTokenOnWebsocketHandshake(request));

        // Assert
        verify(request).getParameter(headerKey);
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.AccessTokenNotFound.class)
                .hasMessageContaining("JWT access token not found");
    }

    @Test
    void readRequestRefreshToken() throws JbstExceptions.RefreshTokenNotFound {
        // Arrange
        var headerKey = this.jwt().getRefreshToken().getHeaderKey();
        var header = JbstRandom.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(headerKey)).thenReturn(header);

        // Act
        this.componentUnderTest.readRequestRefreshToken(request);

        // Assert
        verify(request).getHeader(headerKey);
        assertThat(request.getHeader(headerKey)).isEqualTo(header);
    }

    @Test
    void readRequestRefreshTokenThrow() {
        // Arrange
        var headerKey = this.jwt().getRefreshToken().getHeaderKey();
        var request = mock(HttpServletRequest.class);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestRefreshToken(request));

        // Assert
        verify(request).getHeader(headerKey);
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.RefreshTokenNotFound.class)
                .hasMessageContaining("JWT refresh token not found");
    }

    @Test
    void readRequestRefreshTokenOnWebsocketHandshake() throws JbstExceptions.RefreshTokenNotFound {
        // Arrange
        var headerKey = this.jwt().getRefreshToken().getHeaderKey();
        var header = JbstRandom.randomString();
        var request = mock(HttpServletRequest.class);
        when(request.getParameter(headerKey)).thenReturn(header);

        // Act
        this.componentUnderTest.readRequestRefreshTokenOnWebsocketHandshake(request);

        // Assert
        verify(request).getParameter(headerKey);
        assertThat(request.getParameter(headerKey)).isEqualTo(header);
    }

    @Test
    void readRequestRefreshTokenOnWebsocketHandshakeThrow() {
        // Arrange
        var headerKey = this.jwt().getRefreshToken().getHeaderKey();
        var request = mock(HttpServletRequest.class);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.readRequestRefreshTokenOnWebsocketHandshake(request));

        // Assert
        verify(request).getParameter(headerKey);
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.RefreshTokenNotFound.class)
                .hasMessageContaining("JWT refresh token not found");
    }

    @Test
    void clearTokens() {
        // Arrange
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.clearTokens(response);

        // Assert
        verifyNoMoreInteractions(response);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private JbstPropertySecurityJWT jwt() {
        return this.jbstProperties.getSecurity().getJwt();
    }
}
