package jbst.iam.filters.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.http.cache.CachedBodyHttpServletRequest;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.utils.JbstHttpUtils;
import jbst.iam.utils.SecurityPrincipalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jbst.foundation.utilities.http.HttpServletRequestUtility.isMultipartRequest;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdvancedRequestLoggingFilter extends OncePerRequestFilter {

    // Utilities
    private final JbstHttpUtils httpUtils;
    private final SecurityPrincipalUtils securityPrincipalUtils;
    // Properties
    private final JbstProperties jbstProperties;

    @SuppressWarnings({"LoggingSimilarMessage", "StringConcatenationArgumentToLogCall"})
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (isMultipartRequest(request)) {
            filterChain.doFilter(request, response);
        } else {
            var cachedRequest = new CachedBodyHttpServletRequest(request);
            this.httpUtils.cachePayload(cachedRequest);

            if (this.jbstProperties.getSecurityJwtConfigs().getLoggingConfigs().isAdvancedRequestLoggingEnabled()) {
                LOGGER.info("============================================================================================");
                LOGGER.info("Method: (@" + cachedRequest.getMethod() + ", " + cachedRequest.getServletPath() + ")");
                LOGGER.info("Current User: " + this.securityPrincipalUtils.getAuthenticatedUsernameOrUnexpected());
                var payload = cachedRequest.getCachedPayload();
                if (!payload.value().isBlank()) {
                    LOGGER.info("Payload: \n" + payload);
                } else {
                    LOGGER.info("No Payload");
                }
                LOGGER.info("============================================================================================");
            }

            filterChain.doFilter(cachedRequest, response);
        }
    }
}
