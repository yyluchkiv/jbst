package jbst.iam.validators;

import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.exceptions.LoginException;

public interface BaseAuthenticationRequestsValidator {
    void validateLoginStandard(RequestUserLogin request);
    UserToken validateLoginMagicLink(RequestMagicLinkToken request) throws LoginException;
}
