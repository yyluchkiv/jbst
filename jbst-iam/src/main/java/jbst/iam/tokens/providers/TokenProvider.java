package jbst.iam.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.RequestAccessToken;
import jbst.foundation.domain.jwt.RequestRefreshToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.CsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.RefreshTokenNotFoundException;

public interface TokenProvider {
    void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws CsrfTokenNotFoundException;
    RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws AccessTokenNotFoundException;
    RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws AccessTokenNotFoundException;
    RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws RefreshTokenNotFoundException;
    RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws RefreshTokenNotFoundException;
    void clearTokens(HttpServletResponse response);
}
