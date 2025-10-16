package jbst.foundation.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.services.TokensContextThrowerService;
import jbst.foundation.services.TokensService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tokens.facade.TokensProvider;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseTokensService implements TokensService {

    // Assistants
    private final JbstJwtUserDetailsService jwtUserDetailsService;
    // Session
    private final JbstSessionRegistry sessionRegistry;
    // Services
    private final TokensContextThrowerService tokensContextThrowerService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;
    // Utilities
    private final JbstSecurityUtils securityUtils;

    @Override
    public JwtUser getJwtUserByAccessTokenOrThrow(
            RequestAccessToken requestAccessToken,
            RequestRefreshToken requestRefreshToken
    ) throws JbstAccessTokenInvalidException, JbstRefreshTokenInvalidException, JbstAccessTokenExpiredException, JbstAccessTokenDbNotFoundException {
        var accessToken = requestAccessToken.getJwtAccessToken();
        var refreshToken = requestRefreshToken.getJwtRefreshToken();

        var accessTokenValidatedClaims = this.tokensContextThrowerService.verifyValidityOrThrow(accessToken);
        this.tokensContextThrowerService.verifyValidityOrThrow(refreshToken);

        this.tokensContextThrowerService.verifyAccessTokenExpirationOrThrow(accessTokenValidatedClaims);
        this.tokensContextThrowerService.verifyDbPresenceOrThrow(accessToken, accessTokenValidatedClaims);

        // JWT Access Token: isValid + isAlive
        return this.jwtUserDetailsService.loadUserByUsername(accessTokenValidatedClaims.username().value());
    }

    @Override
    public ResponseRefreshTokens refreshSessionOrThrow(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JbstRefreshTokenNotFoundException, JbstRefreshTokenInvalidException, JbstRefreshTokenExpiredException, JbstRefreshTokenDbNotFoundException {
        var oldRefreshToken = this.tokensProvider.readRequestRefreshToken(request).getJwtRefreshToken();

        var refreshTokenValidatedClaims = this.tokensContextThrowerService.verifyValidityOrThrow(oldRefreshToken);
        this.tokensContextThrowerService.verifyRefreshTokenExpirationOrThrow(refreshTokenValidatedClaims);
        var refreshTokenValidatedTuple = this.tokensContextThrowerService.verifyDbPresenceOrThrow(oldRefreshToken, refreshTokenValidatedClaims);
        var user = refreshTokenValidatedTuple.a();
        var session = refreshTokenValidatedTuple.b();

        var accessToken = this.securityUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var newRefreshToken = this.securityUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        this.baseUsersSessionsService.refresh(user, session, accessToken, newRefreshToken, request);

        this.tokensProvider.createResponseAccessToken(accessToken, response);
        this.tokensProvider.createResponseRefreshToken(newRefreshToken, response);

        var username = user.username();
        LOGGER.info("JWT refresh token operation was successfully completed. Username: {}", username);

        this.sessionRegistry.renew(username, oldRefreshToken, accessToken, newRefreshToken);

        return new ResponseRefreshTokens(accessToken, newRefreshToken);
    }
}
