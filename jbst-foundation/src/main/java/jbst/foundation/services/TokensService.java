package jbst.foundation.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;

public interface TokensService {
    JwtUser getJwtUserByAccessTokenOrThrow(
            RequestAccessToken requestAccessToken,
            RequestRefreshToken requestRefreshToken
    ) throws AccessTokenInvalidException, RefreshTokenInvalidException, AccessTokenExpiredException, AccessTokenDbNotFoundException;

    ResponseRefreshTokens refreshSessionOrThrow(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws RefreshTokenNotFoundException, RefreshTokenInvalidException, RefreshTokenExpiredException, RefreshTokenDbNotFoundException;
}
