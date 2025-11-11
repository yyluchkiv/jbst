package jbst.foundation.tokens.facade;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.tokens.providers.JbstTokenCookiesProvider;
import jbst.foundation.tokens.providers.JbstTokenHeadersProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstTokensProvider {

    // Providers
    private final JbstTokenCookiesProvider tokensCookiesProvider;
    private final JbstTokenHeadersProvider tokensHeadersProvider;
    // Properties
    private final JbstProperties jbstProperties;

    @Autowired
    public JbstTokensProvider(
            @Qualifier("tokenCookiesProvider") JbstTokenCookiesProvider tokensCookiesProvider,
            @Qualifier("tokenHeadersProvider") JbstTokenHeadersProvider tokensHeadersProvider,
            JbstProperties jbstProperties
    ) {
        this.tokensCookiesProvider = tokensCookiesProvider;
        this.tokensHeadersProvider = tokensHeadersProvider;
        this.jbstProperties = jbstProperties;
    }

    public final void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response) {
        if (this.isCookiesProviderEnabled()) {
            this.tokensCookiesProvider.createResponseAccessToken(jwtAccessToken, response);
        } else {
            this.tokensHeadersProvider.createResponseAccessToken(jwtAccessToken, response);
        }
    }

    public final void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response) {
        if (this.isCookiesProviderEnabled()) {
            this.tokensCookiesProvider.createResponseRefreshToken(jwtRefreshToken, response);
        } else {
            this.tokensHeadersProvider.createResponseRefreshToken(jwtRefreshToken, response);
        }
    }

    public final DefaultCsrfToken readCsrfToken(HttpServletRequest httpRequest) throws JbstCsrfTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readCsrfToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readCsrfToken(httpRequest);
        }
    }

    public final RequestAccessToken readRequestAccessToken(HttpServletRequest httpRequest) throws JbstAccessTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestAccessToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestAccessToken(httpRequest);
        }
    }

    public final RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws JbstAccessTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestAccessTokenOnWebsocketHandshake(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestAccessTokenOnWebsocketHandshake(httpRequest);
        }
    }

    public final RequestRefreshToken readRequestRefreshToken(HttpServletRequest httpRequest) throws JbstRefreshTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestRefreshToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestRefreshToken(httpRequest);
        }
    }

    public final RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws JbstRefreshTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestRefreshTokenOnWebsocketHandshake(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestRefreshTokenOnWebsocketHandshake(httpRequest);
        }
    }

    public final void clearTokens(HttpServletResponse httpResponse) {
        if (this.isCookiesProviderEnabled()) {
            this.tokensCookiesProvider.clearTokens(httpResponse);
        } else {
            this.tokensHeadersProvider.clearTokens(httpResponse);
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public boolean isCookiesProviderEnabled() {
        return this.jbstProperties.getSecurity().getJwtTokensConfigs().getStorageMethod().isCookies();
    }
}
