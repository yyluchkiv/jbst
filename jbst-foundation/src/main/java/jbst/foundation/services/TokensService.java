package jbst.foundation.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.requests.RequestRefreshToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.tokens.*;
import jbst.foundation.domain.jwt.JwtUser;

public interface TokensService {
    JwtUser getJwtUserByAccessTokenOrThrow(
            RequestAccessToken requestAccessToken,
            RequestRefreshToken requestRefreshToken
    ) throws JbstAccessTokenInvalidException, JbstRefreshTokenInvalidException, JbstAccessTokenExpiredException, JbstAccessTokenDbNotFoundException;

    ResponseRefreshTokens refreshSessionOrThrow(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JbstRefreshTokenNotFoundException, JbstRefreshTokenInvalidException, JbstRefreshTokenExpiredException, JbstRefreshTokenDbNotFoundException;
}
