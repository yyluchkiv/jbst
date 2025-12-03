package jbst.foundation.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.services.JbstTokensContextThrowerService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstTokensService {
    // Assistants
    private final JbstJwtUserDetailsService jwtUserDetailsService;
    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final JbstTokensContextThrowerService tokensContextThrowerService;
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;

    public final JwtUser getJwtUserByAccessTokenOrThrow(
            RequestAccessToken requestAccessToken,
            RequestRefreshToken requestRefreshToken
    ) throws JbstExceptions.AccessTokenInvalid, JbstExceptions.RefreshTokenInvalid, JbstExceptions.AccessTokenExpired, JbstExceptions.AccessTokenDbNotFound {
        var accessToken = requestAccessToken.getJwtAccessToken();
        var refreshToken = requestRefreshToken.getJwtRefreshToken();

        var accessTokenValidatedClaims = this.tokensContextThrowerService.verifyValidityOrThrow(accessToken);
        this.tokensContextThrowerService.verifyValidityOrThrow(refreshToken);

        this.tokensContextThrowerService.verifyAccessTokenExpirationOrThrow(accessTokenValidatedClaims);
        this.tokensContextThrowerService.verifyDbPresenceOrThrow(accessToken, accessTokenValidatedClaims);

        // JWT Access Token: isValid + isAlive
        return this.jwtUserDetailsService.loadUserByUsername(accessTokenValidatedClaims.username().value());
    }

    public final ResponseRefreshTokens refreshSessionOrThrow(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JbstExceptions.RefreshTokenNotFound, JbstExceptions.RefreshTokenInvalid, JbstExceptions.RefreshTokenExpired, JbstExceptions.RefreshTokenDbNotFound {
        var oldRefreshToken = this.tokensProvider.readRequestRefreshToken(request).getJwtRefreshToken();

        var refreshTokenValidatedClaims = this.tokensContextThrowerService.verifyValidityOrThrow(oldRefreshToken);
        this.tokensContextThrowerService.verifyRefreshTokenExpirationOrThrow(refreshTokenValidatedClaims);
        var refreshTokenValidatedTuple = this.tokensContextThrowerService.verifyDbPresenceOrThrow(oldRefreshToken, refreshTokenValidatedClaims);
        var user = refreshTokenValidatedTuple.a();
        var session = refreshTokenValidatedTuple.b();

        var accessToken = this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var newRefreshToken = this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        this.usersSessionsService.refresh(user, session, accessToken, newRefreshToken, request);

        this.tokensProvider.createResponseAccessToken(accessToken, response);
        this.tokensProvider.createResponseRefreshToken(newRefreshToken, response);

        var username = user.username();
        LOGGER.info("JWT refresh token operation was successfully completed. Username: {}", username);

        this.sessionRegistry.renew(username, oldRefreshToken, accessToken, newRefreshToken);

        return new ResponseRefreshTokens(accessToken, newRefreshToken);
    }
}
