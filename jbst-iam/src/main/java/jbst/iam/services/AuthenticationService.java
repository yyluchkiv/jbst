package jbst.iam.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.TokenUnauthorizedException;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.iam.domain.security.CurrentClientUser;

public interface AuthenticationService {
    CurrentClientUser asStandard(UsernamePasswordCredentials credentials, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException;
    CurrentClientUser asMagicLink(JbstUserToken userToken, RequestMagicLinkToken request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException;
    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AccessTokenNotFoundException;
    ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws TokenUnauthorizedException;
}
