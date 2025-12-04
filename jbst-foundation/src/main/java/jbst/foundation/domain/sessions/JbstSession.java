package jbst.foundation.domain.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;

public record JbstSession(
        Username username,
        JwtAccessToken accessToken,
        JwtRefreshToken refreshToken
) {
}

