package jbst.iam.handshakes;

import jakarta.servlet.http.HttpServletRequest;
import jbst.iam.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.socket.WebSocketHandler;
import jbst.foundation.domain.exceptions.tokens.CsrfTokenNotFoundException;
import jbst.foundation.utilities.random.EntityUtility;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class CsrfInterceptorHandshakeTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        TokensProvider tokensProvider() {
            return mock(TokensProvider.class);
        }

        @Bean
        CsrfInterceptorHandshake csrfInterceptorHandshake() {
            return new CsrfInterceptorHandshake(
                    this.tokensProvider()
            );
        }
    }

    // Tokens
    private final TokensProvider tokensProvider;

    private final CsrfInterceptorHandshake componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.tokensProvider
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.tokensProvider
        );
    }

    @Test
    void beforeHandshakeRuntimeExceptionTest() throws CsrfTokenNotFoundException {
        // Arrange
        var request = mock(ServletServerHttpRequest.class);
        var httpRequest = mock(HttpServletRequest.class);
        var response = mock(ServerHttpResponse.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        when(request.getServletRequest()).thenReturn(httpRequest);
        when(this.tokensProvider.readCsrfToken(httpRequest)).thenThrow(new NullPointerException("Test exception"));

        // Act
        var actual = this.componentUnderTest.beforeHandshake(request, response, wsHandler, attributes);

        // Assert
        assertThat(actual).isFalse();
        assertThat(attributes).isEmpty();
        verify(request).getServletRequest();
        verify(this.tokensProvider).readCsrfToken(httpRequest);
        verifyNoMoreInteractions(
                request,
                request,
                wsHandler
        );
    }

    @Test
    void beforeHandshakeNoTokenExceptionTest() throws CsrfTokenNotFoundException {
        // Arrange
        var request = mock(ServletServerHttpRequest.class);
        var httpRequest = mock(HttpServletRequest.class);
        var response = mock(ServerHttpResponse.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        when(request.getServletRequest()).thenReturn(httpRequest);
        when(this.tokensProvider.readCsrfToken(httpRequest)).thenThrow(new CsrfTokenNotFoundException());

        // Act
        var actual = this.componentUnderTest.beforeHandshake(request, response, wsHandler, attributes);

        // Assert
        assertThat(actual).isFalse();
        assertThat(attributes).isEmpty();
        verify(request).getServletRequest();
        verify(this.tokensProvider).readCsrfToken(httpRequest);
        verifyNoMoreInteractions(
                request,
                request,
                wsHandler
        );
    }

    @Test
    void beforeHandshakeTest() throws CsrfTokenNotFoundException {
        // Arrange
        var request = mock(ServletServerHttpRequest.class);
        var httpRequest = mock(HttpServletRequest.class);
        var response = mock(ServerHttpResponse.class);
        var wsHandler = mock(WebSocketHandler.class);
        var defaultCsrfToken = EntityUtility.entity(DefaultCsrfToken.class);
        when(this.tokensProvider.readCsrfToken(httpRequest)).thenReturn(defaultCsrfToken);
        Map<String, Object> attributes = new HashMap<>();
        when(request.getServletRequest()).thenReturn(httpRequest);

        // Act
        var actual = this.componentUnderTest.beforeHandshake(request, response, wsHandler, attributes);

        // Assert
        assertThat(actual).isTrue();
        assertThat(attributes)
                .hasSize(1)
                .containsEntry(CsrfToken.class.getName(), defaultCsrfToken);
        verify(request).getServletRequest();
        verify(this.tokensProvider).readCsrfToken(httpRequest);
        verifyNoMoreInteractions(
                request,
                request,
                wsHandler
        );
    }


    @Test
    void afterHandshakeTest() {
        // Arrange
        var request = mock(ServerHttpRequest.class);
        var response = mock(ServerHttpResponse.class);
        var wsHandler = mock(WebSocketHandler.class);
        var exception = mock(Exception.class);

        // Act
        this.componentUnderTest.afterHandshake(request, response, wsHandler, exception);

        // Assert
        verifyNoMoreInteractions(
                request,
                request,
                wsHandler,
                exception
        );
    }
}
