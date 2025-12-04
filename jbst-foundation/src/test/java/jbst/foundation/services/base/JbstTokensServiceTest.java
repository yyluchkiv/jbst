package jbst.foundation.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.requests.JbstRequestRefreshToken;
import jbst.foundation.domain.dto.responses.JbstResponseRefreshTokens;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.services.JbstTokensContextThrowerService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
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
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.random.JbstRandom.validClaims;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstTokensServiceTest {

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
        JbstTokensContextThrowerService tokenContextThrowerService() {
            return mock(JbstTokensContextThrowerService.class);
        }

        @Bean
        JbstUsersSessionsService usersSessionsService() {
            return mock(JbstUsersSessionsService.class);
        }

        @Bean
        JbstTokensProvider tokensProvider() {
            return mock(JbstTokensProvider.class);
        }

        @Bean
        JbstSecurityUtils securityUtils() {
            return mock(JbstSecurityUtils.class);
        }

        @Bean
        JbstTokensService tokenService() {
            return new JbstTokensService(
                    this.jwtUserDetailsService(),
                    this.sessionRegistry(),
                    this.tokenContextThrowerService(),
                    this.usersSessionsService(),
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
    private final JbstTokensContextThrowerService tokensContextThrowerService;
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;

    private final JbstTokensService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.jwtUserDetailsService,
                this.sessionRegistry,
                this.tokensContextThrowerService,
                this.usersSessionsService,
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
                this.usersSessionsService,
                this.tokensProvider,
                this.securityUtils
        );
    }

    @Test
    void getJwtUserByAccessTokenOrThrowTest() throws JbstExceptions.AccessTokenInvalid, JbstExceptions.RefreshTokenInvalid, JbstExceptions.AccessTokenExpired, JbstExceptions.AccessTokenDbNotFound {
        // Arrange
        var requestAccessToken = JbstRequestAccessToken.random();
        var requestRefreshToken = JbstRequestRefreshToken.random();
        var accessToken = requestAccessToken.getJwtAccessToken();
        var refreshToken = requestRefreshToken.getJwtRefreshToken();
        var accessTokenValidatedClaims = JbstJwtTokenValidatedClaims.valid(accessToken, validClaims());
        var refreshTokenValidatedClaims = JbstJwtTokenValidatedClaims.valid(refreshToken, validClaims());
        var user = entity(JbstJwtUser.class);
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
    void refreshSessionOrThrowTest() throws JbstExceptions.RefreshTokenNotFound, JbstExceptions.RefreshTokenInvalid, JbstExceptions.RefreshTokenExpired, JbstExceptions.RefreshTokenDbNotFound {
        // Arrange
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var oldRequestRefreshToken = new JbstRequestRefreshToken(randomString());
        var oldRefreshToken = oldRequestRefreshToken.getJwtRefreshToken();
        var validatedClaims = JbstJwtTokenValidatedClaims.valid(oldRefreshToken, validClaims());
        var user = entity(JbstJwtUser.class);
        var session = randomPersistedSession();
        var newAccessToken = JbstJwtAccessToken.random();
        var newRefreshToken = JbstJwtRefreshToken.random();

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
        verify(this.usersSessionsService).refresh(user, session, newAccessToken, newRefreshToken, request);
        verify(this.tokensProvider).createResponseAccessToken(newAccessToken, response);
        verify(this.tokensProvider).createResponseRefreshToken(newRefreshToken, response);
        verify(this.sessionRegistry).renew(user.username(), oldRefreshToken, newAccessToken, newRefreshToken);
        assertThat(responseUserSession1).isEqualTo(new JbstResponseRefreshTokens(newAccessToken, newRefreshToken));
    }
}
