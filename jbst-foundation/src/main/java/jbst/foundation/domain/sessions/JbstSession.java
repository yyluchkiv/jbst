package jbst.foundation.domain.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

public record JbstSession(
        Username username,
        JbstJwtAccessToken accessToken,
        JbstJwtRefreshToken refreshToken
) {
}

