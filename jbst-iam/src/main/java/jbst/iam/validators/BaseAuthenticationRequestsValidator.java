package jbst.iam.validators;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;

public interface BaseAuthenticationRequestsValidator {
    UsernamePasswordCredentials validateLoginStandard(RequestUserLogin request);
    JbstUserToken validateLoginMagicLink(RequestMagicLinkToken request) throws JbstLoginException;
}
