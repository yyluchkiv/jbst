package jbst.iam.validators;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;

public interface BaseAuthenticationRequestsValidator {
    UsernamePasswordCredentials validateLoginStandard(RequestUserLogin request);
    UserToken validateLoginMagicLink(RequestMagicLinkToken request) throws JbstLoginException;
}
