package jbst.foundation.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.userdetails.JbstUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.responses.JbstResponseRefreshTokens;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.events.JbstEventAuthenticationLoginFailure;
import jbst.foundation.domain.events.JbstEventAuthenticationMagicLinkFailure;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.http.requests.JbstUserAgentHeader;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;
import jbst.foundation.domain.sessions.JbstSession;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.getUserProcess;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.enums.JbstStatus.STARTED;
import static jbst.foundation.domain.http.JbstHttpServletRequests.getClientIpAddr;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstAuthenticationService {

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Assistants
    private final JbstUserDetailsService jwtUserDetailsService;
    // Sessions
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstUsersService usersService;
    private final JbstUsersSessionsService usersSessionsService;
    private final JbstTokensService tokensService;
    // Repositories
    private final JbstUsersTokensRepository usersTokensRepository;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;
    // Publishers
    private final JbstEventsPublisher eventsPublisher;

    public final Username asStandard(UsernamePasswordCredentials credentials, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.Login {
        try {
            this.asAuthentication(
                    JbstUserCreationOption.STANDARD,
                    credentials,
                    httpRequest,
                    httpResponse
            );
            return credentials.username();
        } catch (BadCredentialsException ex) {
            this.eventsPublisher.publishAuthenticationLoginFailure(
                    new JbstEventAuthenticationLoginFailure(
                            credentials.username(),
                            credentials.password(),
                            getClientIpAddr(httpRequest),
                            new JbstUserAgentHeader(httpRequest)
                    )
            );
            throw new JbstExceptions.Login(ex.getMessage());
        }
    }

    public final Username asMagicLink(JbstMagicLinkUserCredentials magicLinkUserCredentials, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.Login {
        try {
            var credentials = this.usersService.saveOrGetMagicLinkCredentials(magicLinkUserCredentials);
            this.usersTokensRepository.saveAs(magicLinkUserCredentials.userToken().withUsed(true));
            this.asAuthentication(
                    JbstUserCreationOption.MAGICLINK,
                    credentials,
                    httpRequest,
                    httpResponse
            );
            return credentials.username();
        } catch (BadCredentialsException ex) {
            this.eventsPublisher.publishAuthenticationLoginMagicLinkFailure(
                    new JbstEventAuthenticationMagicLinkFailure(
                            magicLinkUserCredentials.userToken(),
                            getClientIpAddr(httpRequest),
                            new JbstUserAgentHeader(httpRequest)
                    )
            );
            throw new JbstExceptions.Login(ex.getMessage());
        }
    }

    public final void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.AccessTokenNotFound {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        if (nonNull(cookie.value())) {
            var accessToken = cookie.getJwtAccessToken();
            var validatedClaims = this.securityUtils.validate(accessToken);
            if (validatedClaims.valid()) {
                var username = validatedClaims.username();
                this.sessionRegistry.logout(username, accessToken);
                this.tokensProvider.clearTokens(httpResponse);
                SecurityContextHolder.clearContext();
                var session = httpRequest.getSession(false);
                if (nonNull(session)) {
                    session.invalidate();
                }
                LOGGER.debug(getUserProcess(username, "Logout Attempt", COMPLETED));
            }
        }
    }

    public final JbstResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.Unauthorized {
        try {
            return this.tokensService.refreshSessionOrThrow(httpRequest, httpResponse);
        } catch (
                JbstExceptions.RefreshTokenNotFound |
                JbstExceptions.RefreshTokenInvalid |
                JbstExceptions.RefreshTokenExpired |
                JbstExceptions.RefreshTokenDbNotFound ex
        ) {
            this.tokensProvider.clearTokens(httpResponse);
            throw new JbstExceptions.Unauthorized(ex.getMessage());
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public void asAuthentication(
            JbstUserCreationOption userCreationOption,
            UsernamePasswordCredentials credentials,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        var username = credentials.username();
        LOGGER.debug(getUserProcess(username, "Authentication as-'%s' attempt".formatted(userCreationOption.getValue()), STARTED));

        var authentication = this.authenticationManager.authenticate(credentials.getAuthenticationToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var user = this.jwtUserDetailsService.loadUserByUsername(username.value());
        if (!user.creationOption().is(userCreationOption)) {
            throw new BadCredentialsException("Unexpected user creation option: %s".formatted(user.creationOption().name()));
        }

        var accessToken = this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var refreshToken = this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        this.usersSessionsService.save(user, accessToken, refreshToken, httpRequest);

        this.tokensProvider.createResponseAccessToken(accessToken, httpResponse);
        this.tokensProvider.createResponseRefreshToken(refreshToken, httpResponse);

        LOGGER.debug(getUserProcess(username, "Authentication as-'%s' attempt".formatted(userCreationOption.getValue()), COMPLETED));

        this.sessionRegistry.register(new JbstSession(username, accessToken, refreshToken));
    }
}
