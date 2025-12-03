package jbst.foundation.services.abstracts;

import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static jbst.foundation.domain.jwt.JwtTokenValidatedClaims.invalid;
import static jbst.foundation.domain.jwt.JwtTokenValidatedClaims.valid;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.expiredClaims;
import static jbst.foundation.domain.random.JbstRandom.validClaims;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractJbstTokensContextThrowerServiceTest {

    private static Stream<Arguments> verifyAccessTokenExpirationOrThrow() {
        return Stream.of(
                Arguments.of(valid(JwtAccessToken.random(), validClaims()), false),
                Arguments.of(valid(JwtRefreshToken.random(), validClaims()), false),
                Arguments.of(valid(JwtAccessToken.random(), expiredClaims()), true),
                Arguments.of(valid(JwtRefreshToken.random(), expiredClaims()), false)
        );
    }

    private static Stream<Arguments> verifyRefreshTokenExpirationOrThrowTest() {
        return Stream.of(
                Arguments.of(valid(JwtAccessToken.random(), validClaims()), false),
                Arguments.of(valid(JwtRefreshToken.random(), validClaims()), false),
                Arguments.of(valid(JwtAccessToken.random(), expiredClaims()), false),
                Arguments.of(valid(JwtRefreshToken.random(), expiredClaims()), true)
        );
    }

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        JbstJwtUserDetailsService jwtUserDetailsService() {
            return mock(JbstJwtUserDetailsService.class);
        }

        @Bean
        JbstUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstUsersSessionsRepository.class);
        }

        @Bean
        JbstSecurityUtils securityUtils() {
            return mock(JbstSecurityUtils.class);
        }

        @Bean
        AbstractJbstTokensContextThrowerService abstractTokensContextThrowerService() {
            return new AbstractJbstTokensContextThrowerService(
                    this.jwtUserDetailsService(),
                    this.usersSessionsRepository(),
                    this.securityUtils()
            ) {};
        }
    }

    // Assistants
    private final JbstJwtUserDetailsService jwtUserDetailsService;
    // Repositories
    private final JbstUsersSessionsRepository usersSessionsRepository;
    // Utilities
    private final JbstSecurityUtils securityUtils;

    private final AbstractJbstTokensContextThrowerService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.securityUtils
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.securityUtils
        );
    }

    @Test
    void verifyValidityAccessTokenTest() throws JbstAccessTokenInvalidException {
        // Arrange
        var accessToken = JwtAccessToken.random();
        var validatedClaims = valid(accessToken, validClaims());
        when(this.securityUtils.validate(accessToken)).thenReturn(validatedClaims);

        // Act
        this.componentUnderTest.verifyValidityOrThrow(accessToken);

        // Assert
        verify(this.securityUtils).validate(accessToken);
    }

    @Test
    void verifyValidityAccessTokenThrowTest() {
        // Arrange
        var jwtAccessToken = JwtAccessToken.random();
        var validatedClaims = invalid(jwtAccessToken);
        when(this.securityUtils.validate(jwtAccessToken)).thenReturn(validatedClaims);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyValidityOrThrow(jwtAccessToken));

        // Assert
        verify(this.securityUtils).validate(jwtAccessToken);
        assertThat(throwable)
                .isInstanceOf(JbstAccessTokenInvalidException.class)
                .hasMessageContaining("JWT access token is invalid");
    }

    @Test
    void verifyValidityRefreshTokenTest() throws JbstRefreshTokenInvalidException {
        // Arrange
        var refreshToken = JwtRefreshToken.random();
        var validatedClaims = valid(refreshToken, validClaims());
        when(this.securityUtils.validate(refreshToken)).thenReturn(validatedClaims);

        // Act
        this.componentUnderTest.verifyValidityOrThrow(refreshToken);

        // Assert
        verify(this.securityUtils).validate(refreshToken);
    }

    @Test
    void verifyValidityRefreshTokenThrowTest() {
        // Arrange
        var refreshToken = JwtRefreshToken.random();
        var validatedClaims = invalid(refreshToken);
        when(this.securityUtils.validate(refreshToken)).thenReturn(validatedClaims);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyValidityOrThrow(refreshToken));

        // Assert
        verify(this.securityUtils).validate(refreshToken);
        assertThat(throwable)
                .isInstanceOf(JbstRefreshTokenInvalidException.class)
                .hasMessageContaining("JWT refresh token is invalid");
    }

    @ParameterizedTest
    @MethodSource("verifyAccessTokenExpirationOrThrow")
    void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims, boolean throwableFlag) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyAccessTokenExpirationOrThrow(validatedClaims));

        // Assert
        if (throwableFlag) {
            assertThat(throwable)
                    .isInstanceOf(JbstAccessTokenExpiredException.class)
                    .hasMessageContaining("JWT access token is expired. Username: " + validatedClaims.username());
        }
    }

    @ParameterizedTest
    @MethodSource("verifyRefreshTokenExpirationOrThrowTest")
    void verifyRefreshTokenExpirationOrThrowTest(JwtTokenValidatedClaims validatedClaims, boolean throwableFlag) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyRefreshTokenExpirationOrThrow(validatedClaims));

        // Assert
        if (throwableFlag) {
            assertThat(throwable)
                    .isInstanceOf(JbstRefreshTokenExpiredException.class)
                    .hasMessageContaining("JWT refresh token is expired. Username: " + validatedClaims.username());
        }
    }

    @Test
    void verifyAccessTokenDbPresenceTest() throws JbstAccessTokenDbNotFoundException {
        // Arrange
        var accessToken = JwtAccessToken.random();
        var validatedClaims = valid(accessToken, validClaims());
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(TuplePresence.present(entity(JbstUserSession.class)));

        // Act
        this.componentUnderTest.verifyDbPresenceOrThrow(accessToken, validatedClaims);

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
    }

    @Test
    void verifyAccessTokenDbPresenceThrowTest() {
        // Arrange
        var accessToken = JwtAccessToken.random();
        var validatedClaims = valid(accessToken, validClaims());
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(TuplePresence.absent());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyDbPresenceOrThrow(accessToken, validatedClaims));

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
        assertThat(throwable)
                .isInstanceOf(JbstAccessTokenDbNotFoundException.class)
                .hasMessageContaining("JWT access token is not present in database. Username: " + validatedClaims.username());
    }

    @Test
    void verifyRefreshTokenDbPresenceTest() throws JbstRefreshTokenDbNotFoundException {
        // Arrange
        var refreshToken = JwtRefreshToken.random();
        var validatedClaims = valid(refreshToken, validClaims());
        var user = entity(JwtUser.class);
        var session = entity(JbstUserSession.class);
        when(this.jwtUserDetailsService.loadUserByUsername(validatedClaims.username().value())).thenReturn(user);
        when(this.usersSessionsRepository.isPresent(refreshToken)).thenReturn(TuplePresence.present(session));

        // Act
        var actual = this.componentUnderTest.verifyDbPresenceOrThrow(refreshToken, validatedClaims);

        // Assert
        verify(this.jwtUserDetailsService).loadUserByUsername(validatedClaims.username().value());
        verify(this.usersSessionsRepository).isPresent(refreshToken);
        assertThat(actual.a()).isEqualTo(user);
        assertThat(actual.b()).isEqualTo(session);
    }

    @Test
    void verifyRefreshTokenDbPresenceThrowTest() {
        // Arrange
        var refreshToken = JwtRefreshToken.random();
        var validatedClaims = valid(refreshToken, validClaims());
        when(this.usersSessionsRepository.isPresent(refreshToken)).thenReturn(TuplePresence.absent());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.verifyDbPresenceOrThrow(refreshToken, validatedClaims));

        // Assert
        verify(this.usersSessionsRepository).isPresent(refreshToken);
        assertThat(throwable)
                .isInstanceOf(JbstRefreshTokenDbNotFoundException.class)
                .hasMessageContaining("JWT refresh token is not present in database. Username: " + validatedClaims.username());
    }
}
