package jbst.foundation.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.services.TokensContextThrowerService;
import jbst.foundation.services.TokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.TokensProvider;
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

import static jbst.foundation.domain.databases.JbstUserSession.randomPersistedSession;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.utilities.random.RandomUtility.validClaims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseTokensServiceTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        JbstJwtUserDetailsService jwtUserDetailsService() {
            return mock(JbstJwtUserDetailsService.class);
        }

        @Bean
        JbstSessionRegistry sessionRegistry() {
            return mock(JbstSessionRegistry.class);
        }

        @Bean
        TokensContextThrowerService tokenContextThrowerService() {
            return mock(TokensContextThrowerService.class);
        }

        @Bean
        BaseUsersSessionsService baseUsersSessionsService() {
            return mock(BaseUsersSessionsService.class);
        }

        @Bean
        TokensProvider tokensProvider() {
            return mock(TokensProvider.class);
        }

        @Bean
        JbstSecurityUtils securityUtils() {
            return mock(JbstSecurityUtils.class);
        }

        @Bean
        TokensService tokenService() {
            return new BaseTokensService(
                    this.jwtUserDetailsService(),
                    this.sessionRegistry(),
                    this.tokenContextThrowerService(),
                    this.baseUsersSessionsService(),
                    this.tokensProvider(),
                    this.securityUtils()
            );
        }
    }

    // Assistants
    private final JbstJwtUserDetailsService jwtUserDetailsService;
    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final TokensContextThrowerService tokensContextThrowerService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;

    private final TokensService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.jwtUserDetailsService,
                this.sessionRegistry,
                this.tokensContextThrowerService,
                this.baseUsersSessionsService,
                this.tokensProvider,
                this.securityUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.jwtUserDetailsService,
                this.sessionRegistry,
                this.tokensContextThrowerService,
                this.baseUsersSessionsService,
                this.tokensProvider,
                this.securityUtils
        );
    }

    @Test
    void getJwtUserByAccessTokenOrThrowTest() throws JbstAccessTokenInvalidException, JbstRefreshTokenInvalidException, JbstAccessTokenExpiredException, JbstAccessTokenDbNotFoundException {
        // Arrange
        var requestAccessToken = RequestAccessToken.random();
        var requestRefreshToken = RequestRefreshToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var refreshToken = requestRefreshToken.getJwtRefreshToken();
        var accessTokenValidatedClaims = JwtTokenValidatedClaims.valid(accessToken, validClaims());
        var refreshTokenValidatedClaims = JwtTokenValidatedClaims.valid(refreshToken, validClaims());
        var user = entity(JwtUser.class);
        when(this.tokensContextThrowerService.verifyValidityOrThrow(accessToken)).thenReturn(accessTokenValidatedClaims);
        when(this.tokensContextThrowerService.verifyValidityOrThrow(refreshToken)).thenReturn(refreshTokenValidatedClaims);
        when(this.jwtUserDetailsService.loadUserByUsername(accessTokenValidatedClaims.username().value())).thenReturn(user);

        // Act
        var actual = this.componentUnderTest.getJwtUserByAccessTokenOrThrow(requestAccessToken, requestRefreshToken);

        // Assert
        verify(this.tokensContextThrowerService).verifyValidityOrThrow(accessToken);
        verify(this.tokensContextThrowerService).verifyValidityOrThrow(refreshToken);
        verify(this.tokensContextThrowerService).verifyAccessTokenExpirationOrThrow(accessTokenValidatedClaims);
        verify(this.tokensContextThrowerService).verifyDbPresenceOrThrow(accessToken, accessTokenValidatedClaims);
        verify(this.jwtUserDetailsService).loadUserByUsername(accessTokenValidatedClaims.username().value());
        assertThat(actual).isEqualTo(user);
    }

    @Test
    void refreshSessionOrThrowTest() throws JbstRefreshTokenNotFoundException, JbstRefreshTokenInvalidException, JbstRefreshTokenExpiredException, JbstRefreshTokenDbNotFoundException {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var oldRequestRefreshToken = new RequestRefreshToken(randomString());
        var oldRefreshToken = oldRequestRefreshToken.getJwtRefreshToken();
        var validatedClaims = JwtTokenValidatedClaims.valid(oldRefreshToken, validClaims());
        var user = entity(JwtUser.class);
        var session = randomPersistedSession();
        var newAccessToken = JwtAccessToken.random();
        var newRefreshToken = JwtRefreshToken.random();

        when(this.tokensProvider.readRequestRefreshToken(request)).thenReturn(oldRequestRefreshToken);
        when(this.tokensContextThrowerService.verifyValidityOrThrow(oldRefreshToken)).thenReturn(validatedClaims);
        when(this.tokensContextThrowerService.verifyDbPresenceOrThrow(oldRefreshToken, validatedClaims)).thenReturn(new Tuple2<>(user, session));
        when(this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams())).thenReturn(newAccessToken);
        when(this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams())).thenReturn(newRefreshToken);

        // Act
        var responseUserSession1 = this.componentUnderTest.refreshSessionOrThrow(request, response);

        // Assert
        verify(this.tokensProvider).readRequestRefreshToken(request);
        verify(this.tokensContextThrowerService).verifyValidityOrThrow(oldRefreshToken);
        verify(this.tokensContextThrowerService).verifyRefreshTokenExpirationOrThrow(validatedClaims);
        verify(this.tokensContextThrowerService).verifyDbPresenceOrThrow(oldRefreshToken, validatedClaims);
        verify(this.securityUtils).createJwtAccessToken(user.getJwtTokenCreationParams());
        verify(this.securityUtils).createJwtRefreshToken(user.getJwtTokenCreationParams());
        verify(this.baseUsersSessionsService).refresh(user, session, newAccessToken, newRefreshToken, request);
        verify(this.tokensProvider).createResponseAccessToken(newAccessToken, response);
        verify(this.tokensProvider).createResponseRefreshToken(newRefreshToken, response);
        verify(this.sessionRegistry).renew(user.username(), oldRefreshToken, newAccessToken, newRefreshToken);
        assertThat(responseUserSession1).isEqualTo(new ResponseRefreshTokens(newAccessToken, newRefreshToken));
    }
}
