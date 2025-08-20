package jbst.iam.utils;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.http.cache.CachedBodyHttpServletRequest;

public interface HttpRequestUtils {
    boolean isCachedEndpoint(HttpServletRequest request);
    void cachePayload(CachedBodyHttpServletRequest cachedRequest);
    String getCachedPayload(HttpServletRequest request);
    boolean isAuthenticationAuthenticateStandardEndpoint(HttpServletRequest request);
    boolean isAuthenticationAuthenticateMagicLinkEndpoint(HttpServletRequest request);
    boolean isAuthenticationRefreshTokenEndpoint(HttpServletRequest request);
}
