package jbst.iam.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.foundation.utils.JbstSecurityUtils;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.events.EventAuthenticationLoginFailure;
import jbst.foundation.domain.events.EventAuthenticationMagicLinkFailure;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.domain.sessions.Session;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.UsersTokensRepository;
import jbst.iam.services.AuthenticationService;
import jbst.iam.services.BaseUsersService;
import jbst.iam.services.BaseUsersSessionsService;
import jbst.iam.services.TokensService;
import jbst.iam.sessions.SessionRegistry;
import jbst.foundation.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Logs.getUserProcess;
import static jbst.foundation.domain.enums.Status.COMPLETED;
import static jbst.foundation.domain.enums.Status.STARTED;
import static jbst.foundation.utilities.http.HttpServletRequestUtility.getClientIpAddr;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationServiceImpl implements AuthenticationService {

    // Authentication
    private final AuthenticationManager authenticationManager;
    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    private final JwtUserDetailsService jwtUserDetailsService;
    // Sessions
    private final SessionRegistry sessionRegistry;
    // Services
    private final BaseUsersService baseUsersService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    private final TokensService tokensService;
    // Repositories
    private final UsersTokensRepository usersTokensRepository;
    // Tokens
    private final TokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtPublisher;

    @Override
    public CurrentClientUser asStandard(UsernamePasswordCredentials credentials, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException {
        try {
            return this.asAuthentication(
                    UserCreationOption.STANDARD,
                    credentials.username(),
                    credentials.password(),
                    httpRequest,
                    httpResponse
            );
        } catch (BadCredentialsException ex) {
            this.securityJwtPublisher.publishAuthenticationLoginFailure(
                    new EventAuthenticationLoginFailure(
                            credentials.username(),
                            credentials.password(),
                            getClientIpAddr(httpRequest),
                            new UserAgentHeader(httpRequest)
                    )
            );
            throw new JbstLoginException(ex.getMessage());
        }
    }

    @Override
    public CurrentClientUser asMagicLink(JbstUserToken userToken, RequestMagicLinkToken request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException {
        try {
            var userCreationOption = UserCreationOption.MAGICLINK;
            var user = this.baseUsersService.safeSave(
                    userCreationOption,
                    userToken.email(),
                    request.zoneId()
            );
            this.usersTokensRepository.saveAs(userToken.withUsed(true));
            return this.asAuthentication(
                    userCreationOption,
                    user.username(),
                    user.password(),
                    httpRequest,
                    httpResponse
            );
        } catch (BadCredentialsException ex) {
            this.securityJwtPublisher.publishAuthenticationLoginMagicLinkFailure(
                    new EventAuthenticationMagicLinkFailure(
                            userToken,
                            getClientIpAddr(httpRequest),
                            new UserAgentHeader(httpRequest)
                    )
            );
            throw new JbstLoginException(ex.getMessage());
        }
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AccessTokenNotFoundException {
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

    @Override
    public ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws TokenUnauthorizedException {
        try {
            return this.tokensService.refreshSessionOrThrow(httpRequest, httpResponse);
        } catch (
                RefreshTokenNotFoundException |
                RefreshTokenInvalidException |
                RefreshTokenExpiredException |
                RefreshTokenDbNotFoundException ex
        ) {
            this.tokensProvider.clearTokens(httpResponse);
            throw new TokenUnauthorizedException(ex.getMessage());
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    public CurrentClientUser asAuthentication(
            UserCreationOption userCreationOption,
            Username username,
            Password password,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        LOGGER.debug(getUserProcess(username, "Authentication as-'%s' attempt".formatted(userCreationOption.getValue()), STARTED));

        var authenticationToken = new UsernamePasswordAuthenticationToken(username.value(), password.value());
        var authentication = this.authenticationManager.authenticate(authenticationToken);

        var user = this.jwtUserDetailsService.loadUserByUsername(username.value());
        if (!user.creationOption().is(userCreationOption)) {
            throw new BadCredentialsException("Unexpected user creation option: %s".formatted(user.creationOption().name()));
        }

        var accessToken = this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var refreshToken = this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        this.baseUsersSessionsService.save(user, accessToken, refreshToken, httpRequest);

        this.tokensProvider.createResponseAccessToken(accessToken, httpResponse);
        this.tokensProvider.createResponseRefreshToken(refreshToken, httpResponse);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOGGER.debug(getUserProcess(username, "Authentication as-'%s' attempt".formatted(userCreationOption.getValue()), COMPLETED));

        this.sessionRegistry.register(new Session(username, accessToken, refreshToken));

        return this.currentSessionAssistant.getCurrentClientUser();
    }
}
