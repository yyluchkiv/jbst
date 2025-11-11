package jbst.foundation.tokens.facade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyJwtToken;
import jbst.foundation.domain.enums.JbstJwtTokenStorageMethod;
import jbst.foundation.domain.properties.base.JbstPropertyTimeAmount;
import jbst.foundation.domain.properties.configs.JbstPropertySecurity;
import jbst.foundation.domain.properties.configs.security.*;
import jbst.foundation.tokens.providers.JbstTokenCookiesProvider;
import jbst.foundation.tokens.providers.JbstTokenHeadersProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
class JbstTokensProviderTest {

    private static Stream<Arguments> jwtTokenStoragesArgs() {
        return Stream.of(
                Arguments.of(JbstJwtTokenStorageMethod.COOKIES),
                Arguments.of(JbstJwtTokenStorageMethod.HEADERS)
        );
    }

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean("tokensCookiesProvider")
        JbstTokenCookiesProvider tokensCookiesProvider() {
            return mock(JbstTokenCookiesProvider.class);
        }

        @Bean("tokensHeadersProvider")
        JbstTokenHeadersProvider tokensHeadersProvider() {
            return mock(JbstTokenHeadersProvider.class);
        }

        @Bean
        JbstTokensProvider tokensProvider() {
            return new JbstTokensProvider(
                    this.tokensCookiesProvider(),
                    this.tokensHeadersProvider(),
                    this.jbstProperties()
            );
        }
    }

    private final JbstTokenCookiesProvider tokensCookiesProvider;
    private final JbstTokenHeadersProvider tokensHeadersProvider;
    private final JbstProperties jbstProperties;

    private final JbstTokensProvider componentUnderTest;

    @Autowired
    public JbstTokensProviderTest(
            @Qualifier("tokensCookiesProvider") JbstTokenCookiesProvider tokensCookiesProvider,
            @Qualifier("tokensHeadersProvider") JbstTokenHeadersProvider tokensHeadersProvider,
            JbstProperties jbstProperties,
            JbstTokensProvider tokensProvider
    ) {
        this.tokensCookiesProvider = tokensCookiesProvider;
        this.tokensHeadersProvider = tokensHeadersProvider;
        this.jbstProperties = jbstProperties;
        this.componentUnderTest = tokensProvider;
    }

    @BeforeEach
    void beforeEach() {
        reset(
                this.tokensCookiesProvider,
                this.tokensHeadersProvider,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verify(this.jbstProperties).getSecurity();
        verifyNoMoreInteractions(
                this.tokensCookiesProvider,
                this.tokensHeadersProvider,
                this.jbstProperties
        );
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void createResponseAccessToken(JbstJwtTokenStorageMethod method) {
        // Arrange
        this.mockProperties(method);
        var jwtAccessToken = new JwtAccessToken(randomString());
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.createResponseAccessToken(jwtAccessToken, response);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).createResponseAccessToken(jwtAccessToken, response);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).createResponseAccessToken(jwtAccessToken, response);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void createResponseRefreshToken(JbstJwtTokenStorageMethod method) {
        // Arrange
        this.mockProperties(method);
        var refreshAccessToken = JwtRefreshToken.random();
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.createResponseRefreshToken(refreshAccessToken, response);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).createResponseRefreshToken(refreshAccessToken, response);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).createResponseRefreshToken(refreshAccessToken, response);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void readCsrfToken(JbstJwtTokenStorageMethod method) throws JbstCsrfTokenNotFoundException {
        // Arrange
        this.mockProperties(method);
        var request = mock(HttpServletRequest.class);

        // Act
        this.componentUnderTest.readCsrfToken(request);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).readCsrfToken(request);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).readCsrfToken(request);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void readRequestAccessToken(JbstJwtTokenStorageMethod method) throws JbstAccessTokenNotFoundException {
        // Arrange
        this.mockProperties(method);
        var request = mock(HttpServletRequest.class);

        // Act
        this.componentUnderTest.readRequestAccessToken(request);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).readRequestAccessToken(request);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).readRequestAccessToken(request);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void readRequestAccessTokenOnWebsocketHandshake(JbstJwtTokenStorageMethod method) throws JbstAccessTokenNotFoundException {
        // Arrange
        this.mockProperties(method);
        var request = mock(HttpServletRequest.class);

        // Act
        this.componentUnderTest.readRequestAccessTokenOnWebsocketHandshake(request);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).readRequestAccessTokenOnWebsocketHandshake(request);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).readRequestAccessTokenOnWebsocketHandshake(request);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void readRequestRefreshToken(JbstJwtTokenStorageMethod method) throws JbstRefreshTokenNotFoundException {
        // Arrange
        this.mockProperties(method);
        var request = mock(HttpServletRequest.class);

        // Act
        this.componentUnderTest.readRequestRefreshToken(request);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).readRequestRefreshToken(request);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).readRequestRefreshToken(request);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void readRequestRefreshTokenOnWebsocketHandshake(JbstJwtTokenStorageMethod method) throws JbstRefreshTokenNotFoundException {
        // Arrange
        this.mockProperties(method);
        var request = mock(HttpServletRequest.class);

        // Act
        this.componentUnderTest.readRequestRefreshTokenOnWebsocketHandshake(request);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).readRequestRefreshTokenOnWebsocketHandshake(request);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).readRequestRefreshTokenOnWebsocketHandshake(request);
        }
    }

    @ParameterizedTest
    @MethodSource("jwtTokenStoragesArgs")
    void clearTokens(JbstJwtTokenStorageMethod method) {
        // Arrange
        this.mockProperties(method);
        var response = mock(HttpServletResponse.class);

        // Act
        this.componentUnderTest.clearTokens(response);

        // Assert
        if (method.isCookies()) {
            verify(this.tokensCookiesProvider).clearTokens(response);
        }
        if (method.isHeaders()) {
            verify(this.tokensHeadersProvider).clearTokens(response);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void mockProperties(JbstJwtTokenStorageMethod method) {
        when(this.jbstProperties.getSecurity()).thenReturn(
                new JbstPropertySecurity(
                        JbstPropertySecurityAuthorities.hardcoded(),
                        JbstPropertySecurityCookies.hardcoded(),
                        JbstPropertySecurityEssence.hardcoded(),
                        new JbstPropertySecurityJWT(
                                "JBST",
                                method,
                                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(30L, SECONDS), "ajwt", "T-AJWT"),
                                new JbstPropertyJwtToken(new JbstPropertyTimeAmount(12L, HOURS), "rjwt", "T-RJWT")
                        ),
                        JbstPropertySecurityLogging.hardcoded(),
                        JbstPropertySecuritySessions.hardcoded(),
                        JbstPropertySecurityUsersEmails.hardcoded(),
                        JbstPropertySecurityWebsockets.hardcoded(),
                        JbstPropertySecurityUsersTokens.hardcoded()
                )
        );
    }
}
