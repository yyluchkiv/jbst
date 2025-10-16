package jbst.foundation.services.abstracts;

import jbst.foundation.assistants.userdetails.JbstJwtUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.TokensContextThrowerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTokensContextThrowerService implements TokensContextThrowerService {

    // Assistants
    protected final JbstJwtUserDetailsService jwtUserDetailsService;
    // Repositories
    protected final JbstUsersSessionsRepository usersSessionsRepository;
    // Utilities
    protected final JbstSecurityUtils securityUtils;

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtAccessToken accessToken) throws JbstAccessTokenInvalidException {
        var validatedClaims = this.securityUtils.validate(accessToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new JbstAccessTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public JwtTokenValidatedClaims verifyValidityOrThrow(JwtRefreshToken refreshToken) throws JbstRefreshTokenInvalidException {
        var validatedClaims = this.securityUtils.validate(refreshToken);
        if (validatedClaims.isInvalid()) {
            SecurityContextHolder.clearContext();
            throw new JbstRefreshTokenInvalidException();
        }
        return validatedClaims;
    }

    @Override
    public void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws JbstAccessTokenExpiredException {
        if (validatedClaims.isExpired() && validatedClaims.isAccess()) {
            SecurityContextHolder.clearContext();
            throw new JbstAccessTokenExpiredException(validatedClaims.username());
        }
    }

    @Override
    public void verifyRefreshTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws JbstRefreshTokenExpiredException {
        if (validatedClaims.isExpired() && validatedClaims.isRefresh()) {
            SecurityContextHolder.clearContext();
            throw new JbstRefreshTokenExpiredException(validatedClaims.username());
        }
    }

    @Override
    public void verifyDbPresenceOrThrow(JwtAccessToken accessToken, JwtTokenValidatedClaims validatedClaims) throws JbstAccessTokenDbNotFoundException {
        var username = validatedClaims.username();
        var databasePresence = this.usersSessionsRepository.isPresent(accessToken);
        if (!databasePresence.present()) {
            SecurityContextHolder.clearContext();
            throw new JbstAccessTokenDbNotFoundException(username);
        }
    }

    @Override
    public Tuple2<JwtUser, JbstUserSession> verifyDbPresenceOrThrow(JwtRefreshToken refreshToken, JwtTokenValidatedClaims validatedClaims) throws JbstRefreshTokenDbNotFoundException {
        var username = validatedClaims.username();
        var databasePresence = this.usersSessionsRepository.isPresent(refreshToken);
        if (!databasePresence.present()) {
            SecurityContextHolder.clearContext();
            throw new JbstRefreshTokenDbNotFoundException(username);
        }
        var user = this.jwtUserDetailsService.loadUserByUsername(username.value());
        return new Tuple2<>(user, databasePresence.value());
    }
}
