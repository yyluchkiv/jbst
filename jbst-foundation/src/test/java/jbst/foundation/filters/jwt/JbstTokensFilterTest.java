package jbst.foundation.filters.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.sessions.Session;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.handlers.JbstAccessDeniedHandler;
import jbst.foundation.services.base.JbstTokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstTokensFilterTest {

    private static Stream<Arguments> clearCookieTest() {
        return Stream.of(
                Arguments.of(new JbstAccessTokenInvalidException()),
                Arguments.of(new JbstRefreshTokenInvalidException()),
                Arguments.of(new JbstAccessTokenDbNotFoundException(Username.hardcoded()))
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
        JbstSessionRegistry sessionRegistry() {
            return mock(JbstSessionRegistry.class);
        }

        @Bean
        JbstExtensionService extensionService() {
            return mock(JbstExtensionService.class);
        }

        @Bean
        JbstTokensService tokenService() {
            return mock(JbstTokensService.class);
        }

        @Bean
        JbstTokensProvider tokensProvider() {
            return mock(JbstTokensProvider.class);
        }

        @Bean
        JbstAccessDeniedHandler accessDeniedHandler() {
            return mock(JbstAccessDeniedHandler.class);
        }

        @Bean
        JbstSecurityUtils securityUtils() {
            return mock(JbstSecurityUtils.class);
        }

        @Bean
        JbstTokensFilter jwtAccessTokenFilter() {
            return new JbstTokensFilter(
                    this.sessionRegistry(),
                    this.extensionService(),
                    this.tokenService(),
                    this.tokensProvider(),
                    this.accessDeniedHandler(),
                    this.securityUtils(),
                    this.jbstProperties
            );
        }
    }

    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Extension
    private final JbstExtensionService extensionService;
    // Services
    private final JbstTokensService tokensService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Handlers
    private final JbstAccessDeniedHandler accessDeniedHandler;
    // Utils
    private final JbstSecurityUtils securityUtils;

    private final JbstTokensFilter componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.sessionRegistry,
                this.extensionService,
                this.tokensService,
                this.tokensProvider,
                this.accessDeniedHandler,
                this.securityUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.sessionRegistry,
                this.extensionService,
                this.tokensService,
                this.tokensProvider,
                this.accessDeniedHandler,
                this.securityUtils
        );
    }

    @Test
    void accessTokenNotFoundTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenThrow(new JbstAccessTokenNotFoundException());

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @Test
    void accessTokenExpiredTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenThrow(new JbstAccessTokenExpiredException(Username.hardcoded()));

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @Test
    void refreshTokenNotFoundTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var requestAccessToken = RequestAccessToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenThrow(new JbstRefreshTokenNotFoundException());

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).clearTokens(response);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value());
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @ParameterizedTest
    @MethodSource("clearCookieTest")
    void clearCookieTest(Exception exception) throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenThrow(exception);

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        verify(this.tokensProvider).clearTokens(response);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value());
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @Test
    void accessTokenValidTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var user = entity(JwtUser.class);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenReturn(user);

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        // no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(new Session(user.username(), requestAccessToken.getJwtAccessToken(), requestRefreshToken.getJwtRefreshToken()));
        verify(this.extensionService).doFilter(request);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @Test
    void tokenExtensionUnauthorizedExceptionTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var user = entity(JwtUser.class);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenReturn(user);
        doThrow(new JbstTokenExtensionUnauthorizedException(randomString())).when(this.extensionService).doFilter(request);

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        // no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(new Session(user.username(), requestAccessToken.getJwtAccessToken(), requestRefreshToken.getJwtRefreshToken()));
        verify(this.extensionService).doFilter(request);
        verify(this.tokensProvider).clearTokens(response);
        verify(response).sendError(HttpStatus.UNAUTHORIZED.value());
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }

    @Test
    void tokenExtensionAccessDeniedExceptionTest() throws Exception {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);
        var user = entity(JwtUser.class);
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.tokensProvider.readRequestRefreshToken(any(HttpServletRequest.class))).thenReturn(requestRefreshToken);
        when(this.tokensService.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken)).thenReturn(user);
        var exception = new JbstTokenExtensionAccessDeniedException(randomString());
        doThrow(exception).when(this.extensionService).doFilter(request);

        // Act
        this.componentUnderTest.doFilterInternal(request, response, filterChain);

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.tokensProvider).readRequestRefreshToken(any(HttpServletRequest.class));
        verify(this.tokensService).getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);
        // no verifications on static SecurityContextHolder
        verify(this.sessionRegistry).register(new Session(user.username(), requestAccessToken.getJwtAccessToken(), requestRefreshToken.getJwtRefreshToken()));
        verify(this.extensionService).doFilter(request);
        verify(this.tokensProvider).clearTokens(response);
        var exceptionAC = ArgumentCaptor.forClass(AccessDeniedException.class);
        verify(this.accessDeniedHandler).handle(eq(request), eq(response), exceptionAC.capture());
        assertThat(exceptionAC.getValue().getMessage()).isEqualTo(exception.getMessage());
        verifyNoMoreInteractions(
                request,
                response,
                filterChain
        );
    }
}
