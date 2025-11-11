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
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Qualifier("tokenHeadersProvider")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTokenHeadersProvider implements JbstTokenProvider {

    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void createResponseAccessToken(JwtAccessToken jwtAccessToken, HttpServletResponse response) {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        response.addHeader(headerKey, jwtAccessToken.value());
    }

    @Override
    public void createResponseRefreshToken(JwtRefreshToken jwtRefreshToken, HttpServletResponse response) {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        response.addHeader(headerKey, jwtRefreshToken.value());
    }

    @Override
    public DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstCsrfTokenNotFoundException {
        var csrfConfigs = this.jbstProperties.getSecurity().getWebsockets().getCsrf();
        // WARNING: development workaround to read request query parameters instead of request headers
        var header = request.getParameter(csrfConfigs.getTokenKey());
        if (nonNull(header)) {
            return new DefaultCsrfToken(csrfConfigs.getHeaderName(), csrfConfigs.getParameterName(), header);
        } else {
            throw new JbstCsrfTokenNotFoundException();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstAccessTokenNotFoundException {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        var header = request.getHeader(headerKey);
        if (nonNull(header)) {
            return new RequestAccessToken(header);
        } else {
            throw new JbstAccessTokenNotFoundException();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstAccessTokenNotFoundException {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        // WARNING: development workaround to read request query parameters instead of request headers
        var header = request.getParameter(headerKey);
        if (nonNull(header)) {
            return new RequestAccessToken(header);
        } else {
            throw new JbstAccessTokenNotFoundException();
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstRefreshTokenNotFoundException {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        var header = request.getHeader(headerKey);
        if (nonNull(header)) {
            return new RequestRefreshToken(header);
        } else {
            throw new JbstRefreshTokenNotFoundException();
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstRefreshTokenNotFoundException {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        var header = request.getParameter(headerKey);
        if (nonNull(header)) {
            return new RequestRefreshToken(header);
        } else {
            throw new JbstRefreshTokenNotFoundException();
        }
    }

    @Override
    public void clearTokens(HttpServletResponse response) {
        // headers stored on front-end
    }
}
