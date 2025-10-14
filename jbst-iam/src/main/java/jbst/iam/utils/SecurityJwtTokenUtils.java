package jbst.iam.utils;

import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.iam.domain.jwt.JwtTokenCreationParams;
import jbst.iam.domain.jwt.JwtTokenValidatedClaims;
import jbst.foundation.domain.properties.base.TimeAmount;

public interface SecurityJwtTokenUtils {
    JwtAccessToken createJwtAccessToken(JwtTokenCreationParams creationParams);
    JwtRefreshToken createJwtRefreshToken(JwtTokenCreationParams creationParams);
    String createJwtToken(JwtTokenCreationParams creationParams, TimeAmount timeAmount);
    JwtTokenValidatedClaims validate(JwtAccessToken jwtAccessToken);
    JwtTokenValidatedClaims validate(JwtRefreshToken jwtRefreshToken);
}
