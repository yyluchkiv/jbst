package jbst.foundation.validators;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.security.MagicLinkUserCredentials;

public interface BaseAuthenticationRequestsValidator {
    UsernamePasswordCredentials validateLoginStandard(RequestUserLogin request);
    MagicLinkUserCredentials validateLoginMagicLink(RequestMagicLinkToken request) throws JbstLoginException;
}
