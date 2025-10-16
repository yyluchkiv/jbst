package jbst.server.ops.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLoginFailureUsernamePassword;
import jbst.foundation.utils.UserMetadataUtils;
import jbst.server.ops.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.http.HttpServletRequestUtility.getClientIpAddr;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationIncidentFilter extends OncePerRequestFilter {

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Utils
    private final UserMetadataUtils userMetadataUtils;
    // Properties
    private final ServerProperties serverProperties;

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");
        if (nonNull(authorization) && authorization.toLowerCase().startsWith("basic")) {
            var base64Credentials = authorization.substring("Basic".length()).trim();
            var credentialsDecoded = Base64.decodeBase64(base64Credentials);
            var values = new String(credentialsDecoded, StandardCharsets.UTF_8).split(":", 2);
            var credentials = new UsernamePasswordCredentials(
                    Username.of(values[0]),
                    Password.of(values[1])
            );
            var server = this.serverProperties.getServerConfigs();
            if (!server.containsCredentials(credentials)) {
                var incident = new IncidentAuthenticationLoginFailureUsernamePassword(
                        credentials,
                        this.userMetadataUtils.getUserRequestMetadataProcessed(
                                getClientIpAddr(request),
                                new UserAgentHeader(request)
                        )
                );
                this.applicationEventPublisher.publishEvent(incident.getPlainIncident());
            }
        }
        filterChain.doFilter(request, response);
    }
}
