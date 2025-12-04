package jbst.foundation.services;

import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.jwt.JbstJwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.tuples.Tuple2;

public interface JbstTokensContextThrowerService {
    JbstJwtTokenValidatedClaims verifyValidityOrThrow(JbstJwtAccessToken accessToken) throws JbstExceptions.AccessTokenInvalid;
    JbstJwtTokenValidatedClaims verifyValidityOrThrow(JbstJwtRefreshToken refreshToken) throws JbstExceptions.RefreshTokenInvalid;

    void verifyAccessTokenExpirationOrThrow(JbstJwtTokenValidatedClaims validatedClaims) throws JbstExceptions.AccessTokenExpired;
    void verifyRefreshTokenExpirationOrThrow(JbstJwtTokenValidatedClaims validatedClaims) throws JbstExceptions.RefreshTokenExpired;

    void verifyDbPresenceOrThrow(JbstJwtAccessToken accessToken, JbstJwtTokenValidatedClaims validatedClaims) throws JbstExceptions.AccessTokenDbNotFound;
    Tuple2<JbstJwtUser, JbstUserSession> verifyDbPresenceOrThrow(JbstJwtRefreshToken refreshToken, JbstJwtTokenValidatedClaims validatedClaims) throws JbstExceptions.RefreshTokenDbNotFound;
}
