package jbst.foundation.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.events.EventAuthenticationLoginFailure;
import jbst.foundation.domain.exceptions.ExceptionEntity;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.utils.JbstHttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static jbst.foundation.utilities.http.HttpServletRequestUtility.getClientIpAddr;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Publishers
    private final JbstEventsPublisher eventsPublisher;
    // Utilities
    private final JbstHttpUtils httpUtils;
    // JSONs
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(this.objectMapper.writeValueAsString(new ExceptionEntity(exception)));

        System.out.println("=======================================================================================");
        System.out.println("ENDPOINT: " + this.httpUtils.isCachedEndpoint(request));
        System.out.println("JSON: " + this.objectMapper.writeValueAsString(new ExceptionEntity(exception)));
        System.out.println("=======================================================================================");

        // in case of another endpoint to cache - extract methods like: isLoginEndpoint or isLogoutEndpoint
        if (exception instanceof BadCredentialsException && this.httpUtils.isCachedEndpoint(request)) {
            System.out.println("=======================================================================================");
            System.out.println("REQ: " + this.httpUtils.getCachedPayload(request));
            System.out.println("=======================================================================================");
            var requestUserLogin = this.objectMapper.readValue(
                    this.httpUtils.getCachedPayload(request),
                    RequestUserLogin.class
            );

            this.eventsPublisher.publishAuthenticationLoginFailure(
                    new EventAuthenticationLoginFailure(
                            requestUserLogin.username(),
                            requestUserLogin.password(),
                            getClientIpAddr(request),
                            new UserAgentHeader(request)
                    )
            );
        }
    }
}
