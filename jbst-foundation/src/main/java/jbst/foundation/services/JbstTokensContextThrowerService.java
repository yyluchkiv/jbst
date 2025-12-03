package jbst.foundation.services;

import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.Tuple2;

public interface JbstTokensContextThrowerService {
    JwtTokenValidatedClaims verifyValidityOrThrow(JwtAccessToken accessToken) throws JbstExceptions.AccessTokenInvalid;
    JwtTokenValidatedClaims verifyValidityOrThrow(JwtRefreshToken refreshToken) throws JbstExceptions.RefreshTokenInvalid;

    void verifyAccessTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws JbstExceptions.AccessTokenExpired;
    void verifyRefreshTokenExpirationOrThrow(JwtTokenValidatedClaims validatedClaims) throws JbstExceptions.RefreshTokenExpired;

    void verifyDbPresenceOrThrow(JwtAccessToken accessToken, JwtTokenValidatedClaims validatedClaims) throws JbstExceptions.AccessTokenDbNotFound;
    Tuple2<JwtUser, JbstUserSession> verifyDbPresenceOrThrow(JwtRefreshToken refreshToken, JwtTokenValidatedClaims validatedClaims) throws JbstExceptions.RefreshTokenDbNotFound;
}
