package jbst.iam.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.TokenUnauthorizedException;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.dto.responses.ResponseRefreshTokens;
import jbst.iam.domain.exceptions.LoginException;
import jbst.iam.domain.security.CurrentClientUser;

public interface AuthenticationService {
    CurrentClientUser login(RequestUserLogin request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws LoginException;
    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AccessTokenNotFoundException;
    ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws TokenUnauthorizedException;
}
