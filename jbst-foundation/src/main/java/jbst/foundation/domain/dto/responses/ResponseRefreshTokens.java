package jbst.foundation.domain.dto.responses;

import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;

public record ResponseRefreshTokens(
        JbstJwtAccessToken accessToken,
        JbstJwtRefreshToken refreshToken
) {

    public static ResponseRefreshTokens random() {
        return new ResponseRefreshTokens(JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
    }
}
