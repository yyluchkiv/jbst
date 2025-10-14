package jbst.iam.services.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.iam.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.jwt.RequestAccessToken;
import jbst.foundation.domain.jwt.RequestRefreshToken;
import jbst.iam.services.BaseUsersSessionsService;
import jbst.iam.services.TokensContextThrowerService;
import jbst.iam.services.TokensService;
import jbst.iam.sessions.SessionRegistry;
import jbst.iam.tokens.facade.TokensProvider;
import jbst.iam.utils.SecurityJwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseTokensService implements TokensService {

    // Assistants
    private final JwtUserDetailsService jwtUserDetailsService;
    // Session
    private final SessionRegistry sessionRegistry;
    // Services
    private final TokensContextThrowerService tokensContextThrowerService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;
    // Utilities
    private final SecurityJwtTokenUtils securityJwtTokenUtils;

    @Override
    public JwtUser getJwtUserByAccessTokenOrThrow(
            RequestAccessToken requestAccessToken,
            RequestRefreshToken requestRefreshToken
    ) throws AccessTokenInvalidException, RefreshTokenInvalidException, AccessTokenExpiredException, AccessTokenDbNotFoundException {
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
    ) throws RefreshTokenNotFoundException, RefreshTokenInvalidException, RefreshTokenExpiredException, RefreshTokenDbNotFoundException {
        var oldRefreshToken = this.tokensProvider.readRequestRefreshToken(request).getJwtRefreshToken();

        var refreshTokenValidatedClaims = this.tokensContextThrowerService.verifyValidityOrThrow(oldRefreshToken);
        this.tokensContextThrowerService.verifyRefreshTokenExpirationOrThrow(refreshTokenValidatedClaims);
        var refreshTokenValidatedTuple = this.tokensContextThrowerService.verifyDbPresenceOrThrow(oldRefreshToken, refreshTokenValidatedClaims);
        var user = refreshTokenValidatedTuple.a();
        var session = refreshTokenValidatedTuple.b();

        var accessToken = this.securityJwtTokenUtils.createJwtAccessToken(user.getJwtTokenCreationParams());
        var newRefreshToken = this.securityJwtTokenUtils.createJwtRefreshToken(user.getJwtTokenCreationParams());

        this.baseUsersSessionsService.refresh(user, session, accessToken, newRefreshToken, request);

        this.tokensProvider.createResponseAccessToken(accessToken, response);
        this.tokensProvider.createResponseRefreshToken(newRefreshToken, response);

        var username = user.username();
        LOGGER.info("JWT refresh token operation was successfully completed. Username: {}", username);

        this.sessionRegistry.renew(username, oldRefreshToken, accessToken, newRefreshToken);

        return new ResponseRefreshTokens(accessToken, newRefreshToken);
    }
}
