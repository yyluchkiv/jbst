package jbst.foundation.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstTokenUnauthorizedException;
import jbst.foundation.domain.security.CurrentClientUser;

public interface AuthenticationService {
    CurrentClientUser asStandard(UsernamePasswordCredentials credentials, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException;
    CurrentClientUser asMagicLink(JbstUserToken userToken, RequestMagicLinkToken request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstLoginException;
    void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstAccessTokenNotFoundException;
    ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstTokenUnauthorizedException;
}
