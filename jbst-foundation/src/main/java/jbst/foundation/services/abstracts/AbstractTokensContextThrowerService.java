package jbst.foundation.services.abstracts;

import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.utils.JbstSecurityUtils;
import jbst.iam.assistants.userdetails.JwtUserDetailsService;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.repositories.UsersSessionsRepository;
import jbst.foundation.services.TokensContextThrowerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTokensContextThrowerService implements TokensContextThrowerService {

    // Assistants
    protected final JwtUserDetailsService jwtUserDetailsService;
    // Repositories
    protected final UsersSessionsRepository usersSessionsRepository;
    // Utilities
    protected final JbstSecurityUtils securityUtils;

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtAccessToken accessToken) throws AccessTokenInvalidException {
        var validatedClaims = this.securityUtils.validate(accessToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new AccessTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtRefreshToken refreshToken) throws RefreshTokenInvalidException {
        var validatedClaims = this.securityUtils.validate(refreshToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new RefreshTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws AccessTokenExpiredException {
        if (validatedClaims.isExpired() && validatedClaims.isAccess()) {
            SecurityContextHolder.clearContext();
            throw new AccessTokenExpiredException(validatedClaims.username());
        }
    }

    @Override
    public void verifyRefreshTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws RefreshTokenExpiredException {
        if (validatedClaims.isExpired() && validatedClaims.isRefresh()) {
            SecurityContextHolder.clearContext();
            throw new RefreshTokenExpiredException(validatedClaims.username());
        }
    }

    @Override
    public void verifyDbPresenceOrThrow(JwtAccessToken accessToken, JwtTokenValidatedClaims validatedClaims) throws AccessTokenDbNotFoundException {
        var username = validatedClaims.username();
        var databasePresence = this.usersSessionsRepository.isPresent(accessToken);
        if (!databasePresence.present()) {
            SecurityContextHolder.clearContext();
            throw new AccessTokenDbNotFoundException(username);
        }
    }

    @Override
    public Tuple2<JwtUser, JbstUserSession> verifyDbPresenceOrThrow(JwtRefreshToken refreshToken, JwtTokenValidatedClaims validatedClaims) throws RefreshTokenDbNotFoundException {
        var username = validatedClaims.username();
        var databasePresence = this.usersSessionsRepository.isPresent(refreshToken);
        if (!databasePresence.present()) {
            SecurityContextHolder.clearContext();
            throw new RefreshTokenDbNotFoundException(username);
        }
        var user = this.jwtUserDetailsService.loadUserByUsername(username.value());
        return new Tuple2<>(user, databasePresence.value());
    }
}
