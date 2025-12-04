package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

public record JbstResponseRefreshTokens(
        JbstJwtAccessToken accessToken,
        JbstJwtRefreshToken refreshToken
) {

    public static JbstResponseRefreshTokens random() {
        return new JbstResponseRefreshTokens(JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
    }
}
