package jbst.iam.validators;

import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;

public interface BaseAuthenticationRequestsValidator {
    void validateLoginStandard(RequestUserLogin request);
    void validateLoginMagicLink(RequestMagicLinkToken request);
}
