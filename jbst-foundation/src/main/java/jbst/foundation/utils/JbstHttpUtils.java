package jbst.foundation.utils;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.http.cache.CachedBodyHttpServletRequest;
import jbst.foundation.domain.properties.JbstProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static jbst.foundation.utilities.http.HttpServletRequestUtility.isPOST;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstHttpUtils {
    protected static final String CACHED_PAYLOAD_ATTRIBUTE = "jbst-security-jwt-cached-payload-attribute";

    // Properties
    private final JbstProperties jbstProperties;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public final boolean isCachedEndpoint(HttpServletRequest request) {
        return this.isAuthenticationAuthenticateStandardEndpoint(request) || this.isAuthenticationAuthenticateMagicLinkEndpoint(request);
    }

    public final void cachePayload(CachedBodyHttpServletRequest cachedRequest) {
        if (this.isCachedEndpoint(cachedRequest)) {
            cachedRequest.setAttribute(CACHED_PAYLOAD_ATTRIBUTE, cachedRequest.getCachedPayload().value());
        }
    }

    public final String getCachedPayload(HttpServletRequest request) {
        return (String) request.getAttribute(CACHED_PAYLOAD_ATTRIBUTE);
    }

    public final boolean isAuthenticationAuthenticateStandardEndpoint(HttpServletRequest request) {
        return isPOST(request) && this.isEndpoint(request, "/authentication/login/standard");
    }

    public final boolean isAuthenticationAuthenticateMagicLinkEndpoint(HttpServletRequest request) {
        return isPOST(request) && this.isEndpoint(request, "/authentication/login/magiclink");
    }

    public final boolean isAuthenticationRefreshTokenEndpoint(HttpServletRequest request) {
        return isPOST(request) && this.isEndpoint(request, "/authentication/refreshToken");
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private boolean isEndpoint(HttpServletRequest request, String requestMapping) {
        var requestURI = this.contextPath + this.jbstProperties.getMvcConfigs().getBasePathPrefix() + requestMapping;
        return requestURI.equals(request.getRequestURI());
    }
}
