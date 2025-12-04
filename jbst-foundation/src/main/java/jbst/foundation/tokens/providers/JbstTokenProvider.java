package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

public interface JbstTokenProvider {
    void createResponseAccessToken(JbstJwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JbstJwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstExceptions.CsrfTokenNotFound;
    RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound;
    RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound;
    RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound;
    RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound;
    void clearTokens(HttpServletResponse response);
}
