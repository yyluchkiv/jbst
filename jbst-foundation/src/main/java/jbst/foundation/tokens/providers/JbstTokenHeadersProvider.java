package jbst.foundation.tokens.providers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
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
    public void createResponseAccessToken(JbstJwtAccessToken jwtAccessToken, HttpServletResponse response) {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        response.addHeader(headerKey, jwtAccessToken.value());
    }

    @Override
    public void createResponseRefreshToken(JbstJwtRefreshToken jwtRefreshToken, HttpServletResponse response) {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        response.addHeader(headerKey, jwtRefreshToken.value());
    }

    @Override
    public DefaultCsrfToken readCsrfToken(HttpServletRequest request) throws JbstExceptions.CsrfTokenNotFound {
        var csrfConfigs = this.jbstProperties.getSecurity().getWebsockets().getCsrf();
        // WARNING: development workaround to read request query parameters instead of request headers
        var header = request.getParameter(csrfConfigs.getTokenKey());
        if (nonNull(header)) {
            return new DefaultCsrfToken(csrfConfigs.getHeaderName(), csrfConfigs.getParameterName(), header);
        } else {
            throw new JbstExceptions.CsrfTokenNotFound();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessToken(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        var header = request.getHeader(headerKey);
        if (nonNull(header)) {
            return new RequestAccessToken(header);
        } else {
            throw new JbstExceptions.AccessTokenNotFound();
        }
    }

    @Override
    public RequestAccessToken readRequestAccessTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.AccessTokenNotFound {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getAccessToken().getHeaderKey();
        // WARNING: development workaround to read request query parameters instead of request headers
        var header = request.getParameter(headerKey);
        if (nonNull(header)) {
            return new RequestAccessToken(header);
        } else {
            throw new JbstExceptions.AccessTokenNotFound();
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshToken(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        var header = request.getHeader(headerKey);
        if (nonNull(header)) {
            return new RequestRefreshToken(header);
        } else {
            throw new JbstExceptions.RefreshTokenNotFound();
        }
    }

    @Override
    public RequestRefreshToken readRequestRefreshTokenOnWebsocketHandshake(HttpServletRequest request) throws JbstExceptions.RefreshTokenNotFound {
        var headerKey = this.jbstProperties.getSecurity().getJwt().getRefreshToken().getHeaderKey();
        var header = request.getParameter(headerKey);
        if (nonNull(header)) {
            return new RequestRefreshToken(header);
        } else {
            throw new JbstExceptions.RefreshTokenNotFound();
        }
    }

    @Override
    public void clearTokens(HttpServletResponse response) {
        // headers stored on front-end
    }
}
