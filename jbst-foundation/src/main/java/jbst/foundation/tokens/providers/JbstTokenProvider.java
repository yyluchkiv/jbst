package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.requests.JbstRequestRefreshToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

public interface JbstTokenProvider {
    void createResponseAccessToken(JbstJwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JbstJwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstExceptions.CsrfTokenNotFound;
    JbstRequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound;
    JbstRequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound;
    JbstRequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound;
    JbstRequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound;
    void clearTokens(HttpServletResponse response);
}
