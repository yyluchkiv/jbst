package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;

public interface JbstTokenProvider {
    void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response);
    void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response);
    DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstCsrfTokenNotFoundException;
    RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstAccessTokenNotFoundException;
    RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstAccessTokenNotFoundException;
    RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstRefreshTokenNotFoundException;
    RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstRefreshTokenNotFoundException;
    void clearTokens(HttpServletResponse response);
}
