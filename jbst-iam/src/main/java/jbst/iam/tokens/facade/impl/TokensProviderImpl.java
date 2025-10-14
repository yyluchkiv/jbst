package jbst.iam.tokens.facade.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.iam.domain.jwt.RequestAccessToken;
import jbst.iam.domain.jwt.RequestRefreshToken;
import jbst.iam.tokens.facade.TokensProvider;
import jbst.iam.tokens.providers.TokenCookiesProvider;
import jbst.iam.tokens.providers.TokenHeadersProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Service;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.CsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenNotFoundException;
import jbst.foundation.domain.properties.JbstProperties;

@Slf4j
@Service
public class TokensProviderImpl implements TokensProvider {

    // Providers
    private final TokenCookiesProvider tokensCookiesProvider;
    private final TokenHeadersProvider tokensHeadersProvider;
    // Properties
    private final JbstProperties jbstProperties;

    @Autowired
    public TokensProviderImpl(
            @Qualifier("tokenCookiesProvider") TokenCookiesProvider tokensCookiesProvider,
            @Qualifier("tokenHeadersProvider") TokenHeadersProvider tokensHeadersProvider,
            JbstProperties jbstProperties
    ) {
        this.tokensCookiesProvider = tokensCookiesProvider;
        this.tokensHeadersProvider = tokensHeadersProvider;
        this.jbstProperties = jbstProperties;
    }

    @Override
    public void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response) {
        if (this.isCookiesProviderEnabled()) {
            this.tokensCookiesProvider.createResponseAccessToken(jwtAccessToken, response);
        } else {
            this.tokensHeadersProvider.createResponseAccessToken(jwtAccessToken, response);
        }
    }

    @Override
    public void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response) {
        if (this.isCookiesProviderEnabled()) {
            this.tokensCookiesProvider.createResponseRefreshToken(jwtRefreshToken, response);
        } else {
            this.tokensHeadersProvider.createResponseRefreshToken(jwtRefreshToken, response);
        }
    }

    @Override
    public DefaultCsrfToken readCsrfToken(HttpServletRequest httpRequest) throws CsrfTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readCsrfToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readCsrfToken(httpRequest);
        }
    }

    @Override
    public RequestAccessToken readRequestAccessToken(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestAccessToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestAccessToken(httpRequest);
        }
    }

    @Override
    public RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestAccessTokenOnWebsocketHandshake(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestAccessTokenOnWebsocketHandshake(httpRequest);
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshToken(HttpServletRequest httpRequest) throws RefreshTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestRefreshToken(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestRefreshToken(httpRequest);
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest httpRequest) throws RefreshTokenNotFoundException {
        if (this.isCookiesProviderEnabled()) {
            return this.tokensCookiesProvider.readRequestRefreshTokenOnWebsocketHandshake(httpRequest);
        } else {
            return this.tokensHeadersProvider.readRequestRefreshTokenOnWebsocketHandshake(httpRequest);
        }
    }

    @Override
    public void clearTokens(HttpServletResponse httpResponse) {
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
        return this.jbstProperties.getSecurityJwtConfigs().getJwtTokensConfigs().getStorageMethod().isCookies();
    }
}
