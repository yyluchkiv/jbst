package jbst.iam.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.http.requests.UserAgentHeader;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.dto.responses.ResponseRefreshTokens;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.events.EventAuthenticationLoginFailure;
import jbst.iam.domain.exceptions.LoginException;
import jbst.iam.domain.security.CurrentClientUser;
import jbst.iam.domain.sessions.Session;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.services.AuthenticationService;
import jbst.iam.services.BaseUsersSessionsService;
import jbst.iam.services.TokensService;
import jbst.iam.services.UsersEmailsService;
import jbst.iam.sessions.SessionRegistry;
import jbst.iam.tokens.facade.TokensProvider;
import jbst.iam.utils.SecurityJwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
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
    private final BaseUsersSessionsService baseUsersSessionsService;
    private final TokensService tokensService;
    private final UsersEmailsService usersEmailsService;
    // Repositories
    private final UsersRepository usersRepository;
    private final UsersTokensRepository usersTokensRepository;
    // Tokens
    private final TokensProvider tokensProvider;
    // Utilities
    private final SecurityJwtTokenUtils securityJwtTokenUtils;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtPublisher;

    @Override
    public CurrentClientUser asStandard(RequestUserLogin request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws LoginException {
        try {
            var username = request.username();
            var password = request.password();
            LOGGER.debug(JbstConstants.Logs.getUserProcess(username, "Login Attempt", STARTED));

            var authenticationToken = new UsernamePasswordAuthenticationToken(username.value(), password.value());
            var authentication = this.authenticationManager.authenticate(authenticationToken);

            var user = this.jwtUserDetailsService.loadUserByUsername(username.value());
            if (!user.creationOption().isStandard()) {
                throw new BadCredentialsException("Unexpected user creation option");
            }

            var accessToken = this.securityJwtTokenUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
            var refreshToken = this.securityJwtTokenUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

            this.baseUsersSessionsService.save(user, accessToken, refreshToken, httpRequest);

            this.tokensProvider.createResponseAccessToken(accessToken, httpResponse);
            this.tokensProvider.createResponseRefreshToken(refreshToken, httpResponse);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            LOGGER.debug(JbstConstants.Logs.getUserProcess(username, "Login Attempt", COMPLETED));

            this.sessionRegistry.register(new Session(username, accessToken, refreshToken));

            return this.currentSessionAssistant.getCurrentClientUser();
        } catch (BadCredentialsException ex) {
            this.securityJwtPublisher.publishAuthenticationLoginFailure(
                    new EventAuthenticationLoginFailure(
                            request.username(),
                            request.password(),
                            getClientIpAddr(httpRequest),
                            new UserAgentHeader(httpRequest)
                    )
            );
            throw new LoginException(ex.getMessage());
        }
    }

    @Override
    public CurrentClientUser asMagicLink(RequestMagicLinkToken request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws TokenUnauthorizedException {
        var tokenValue = request.token();

        // Find valid magic link token
        var userToken = this.usersTokensRepository.findByValueAsAny(tokenValue);

        if (userToken == null || !userToken.type().equals(UserTokenType.MAGIC_LINK) || userToken.used() || userToken.isExpired()) {
            throw new TokenUnauthorizedException("Invalid or expired magic link token");
        }

        // Mark token as used
        this.usersTokensRepository.saveAs(userToken.withUsed(true));

        // Find user by email
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(userToken.email());
        if (user == null) {
            throw new TokenUnauthorizedException("User not found for magic link token");
        }

        // Generate JWT tokens
        var accessToken = this.securityJwtTokenUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var refreshToken = this.securityJwtTokenUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        // Save session
        this.baseUsersSessionsService.save(user, accessToken, refreshToken, httpRequest);

        // Set cookies
        this.tokensProvider.createResponseAccessToken(accessToken, httpResponse);
        this.tokensProvider.createResponseRefreshToken(refreshToken, httpResponse);

        // Register session
        this.sessionRegistry.register(new Session(user.username(), accessToken, refreshToken));

        LOGGER.debug("User authenticated via magic link: {}", user.username().value());

        // Return current client user
        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @Override
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        if (nonNull(cookie.value())) {
            var accessToken = cookie.getJwtAccessToken();
            var validatedClaims = this.securityJwtTokenUtils.validate(accessToken);
            if (validatedClaims.valid()) {
                var username = validatedClaims.username();
                this.sessionRegistry.logout(username, accessToken);
                this.tokensProvider.clearTokens(httpResponse);
                SecurityContextHolder.clearContext();
                var session = httpRequest.getSession(false);
                if (nonNull(session)) {
                    session.invalidate();
                }
                LOGGER.debug(JbstConstants.Logs.getUserProcess(username, "Logout Attempt", COMPLETED));
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

}
