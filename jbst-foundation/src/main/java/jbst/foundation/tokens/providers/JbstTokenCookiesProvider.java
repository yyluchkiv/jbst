package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.cookies.JbstCookieNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstCsrfTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstRefreshTokenNotFoundException;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.http.JbstHttpCookies.*;
import static jbst.foundation.utilities.numbers.LongUtility.toIntExactOrZeroOnOverflow;

@Slf4j
@Service
@Qualifier("tokenCookiesProvider")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTokenCookiesProvider implements JbstTokenProvider {

    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response) {
        var security = this.jbstProperties.getSecurity();
        var accessToken = security.getJwt().getAccessToken();
        var jwtAccessTokenCookieCreationLatency = security.getCookies().getJwtAccessTokenCookieCreationLatency();
        var maxAge = accessToken.getExpiration().getTimeAmount().toSeconds() - jwtAccessTokenCookieCreationLatency.getTimeAmount().toSeconds();

        var cookie = createCookie(
                accessToken.getCookieKey(),
                jwtAccessToken.value(),
                security.getCookies().getDomain(),
                true,
                toIntExactOrZeroOnOverflow(maxAge)
        );

        response.addCookie(cookie);
    }

    @Override
    public void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response) {
        var security = this.jbstProperties.getSecurity();
        var refreshTokenConfiguration = security.getJwt().getRefreshToken();

        var cookie = createCookie(
                refreshTokenConfiguration.getCookieKey(),
                jwtRefreshToken.value(),
                security.getCookies().getDomain(),
                true,
                toIntExactOrZeroOnOverflow(refreshTokenConfiguration.getExpiration().getTimeAmount().toSeconds())
        );

        response.addCookie(cookie);
    }

    @Override
    public DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstCsrfTokenNotFoundException {
        try {
            var csrfConfigs = this.jbstProperties.getSecurity().getWebsockets().getCsrf();
            // WARNING: security concerns? based on https://github.com/sockjs/sockjs-node#authorisation
            // GitHub issue: https://github.com/sockjs/sockjs-client/issues/196
            var csrfCookie = readCookie(request, csrfConfigs.getTokenKey());
            return new DefaultCsrfToken(csrfConfigs.getHeaderName(), csrfConfigs.getParameterName(), csrfCookie);
        } catch (JbstCookieNotFoundException ex) {
            throw new JbstCsrfTokenNotFoundException();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstAccessTokenNotFoundException {
        try {
            var accessToken = this.jbstProperties.getSecurity().getJwt().getAccessToken();
            var cookie = readCookie(request, accessToken.getCookieKey());
            return new RequestAccessToken(cookie);
        } catch (JbstCookieNotFoundException ex) {
            throw new JbstAccessTokenNotFoundException();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstAccessTokenNotFoundException {
        return this.readRequestAccessToken(request);
    }

    @Override
    public RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstRefreshTokenNotFoundException {
        try {
            var refreshToken = this.jbstProperties.getSecurity().getJwt().getRefreshToken();
            var cookie = readCookie(request, refreshToken.getCookieKey());
            return new RequestRefreshToken(cookie);
        } catch (JbstCookieNotFoundException ex) {
            throw new JbstRefreshTokenNotFoundException();
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstRefreshTokenNotFoundException {
        return this.readRequestRefreshToken(request);
    }

    @Override
    public void clearTokens(HttpServletResponse response) {
        var security = this.jbstProperties.getSecurity();
        var cookies = security.getCookies();
        var accessToken = security.getJwt().getAccessToken();
        var refreshToken = security.getJwt().getRefreshToken();

        response.addCookie(createNullCookie(accessToken.getCookieKey(), cookies.getDomain()));
        response.addCookie(createNullCookie(refreshToken.getCookieKey(), cookies.getDomain()));
    }
}
