package jbst.foundation.handshakes;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstSecurityHandshakeHandlerTest {

    private static Stream<Arguments> determineUserExceptionTest() {
        return Stream.of(
                Arguments.of(new JbstExceptions.AccessTokenInvalid()),
                Arguments.of(new JbstExceptions.RefreshTokenInvalid()),
                Arguments.of(new JbstExceptions.AccessTokenExpired(Username.random()))
        );
    }

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        JbstTokensService tokenService() {
            return mock(JbstTokensService.class);
        }

        @Bean
        JbstTokensProvider tokensProvider() {
            return mock(JbstTokensProvider.class);
        }

        @Bean
        JbstSecurityHandshakeHandler securityHandshakeHandler() {
            return new JbstSecurityHandshakeHandler(
                    this.tokenService(),
                    this.tokensProvider()
            );
        }
    }

    // Services
    private final JbstTokensService tokensService;
    // Tokens
    private final JbstTokensProvider tokensProvider;

    private final JbstSecurityHandshakeHandler componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.tokensService,
                this.tokensProvider
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.tokensService,
                this.tokensProvider
        );
    }

    @ParameterizedTest
    @MethodSource("determineUserExceptionTest")
    void determineUserExceptionTest(Exception exception) throws JbstExceptions.AccessTokenInvalid, JbstExceptions.RefreshTokenInvalid, JbstExceptions.AccessTokenExpired, JbstExceptions.AccessTokenNotFound, JbstExceptions.RefreshTokenNotFound, JbstExceptions.AccessTokenDbNotFound {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var serverHttpRequest = mock(ServletServerHttpRequest.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        when(serverHttpRequest.getServletRequest()).thenReturn(request);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessTokenOnWebsocketHandshake(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshTokenOnWebsocketHandshake(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenThrow(exception);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.determineUser(serverHttpRequest, wsHandler, attributes));

        // Assert
        verify(this.tokensProvider).readRequestAccessTokenOnWebsocketHandshake(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshTokenOnWebsocketHandshake(any(HttpServletRequest.class));
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("WebSocket user not determined");
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
    }

    @Test
    void determineUserTest() throws JbstExceptions.AccessTokenInvalid, JbstExceptions.RefreshTokenInvalid, JbstExceptions.AccessTokenExpired, JbstExceptions.AccessTokenNotFound, JbstExceptions.RefreshTokenNotFound, JbstExceptions.AccessTokenDbNotFound {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var serverHttpRequest = mock(ServletServerHttpRequest.class);
        var wsHandler = mock(WebSocketHandler.class);
        Map<String, Object> attributes = new HashMap<>();
        var user = entity(JwtUser.class);
        when(serverHttpRequest.getServletRequest()).thenReturn(request);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessTokenOnWebsocketHandshake(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshTokenOnWebsocketHandshake(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenReturn(user);

        // Act
        var actual = this.componentUnderTest.determineUser(serverHttpRequest, wsHandler, attributes);

        // Assert
        verify(this.tokensProvider).readRequestAccessTokenOnWebsocketHandshake(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshTokenOnWebsocketHandshake(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(user.getUsername());
    }
}
