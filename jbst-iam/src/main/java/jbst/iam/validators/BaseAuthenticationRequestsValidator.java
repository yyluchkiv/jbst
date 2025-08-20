package jbst.iam.validators;

import jbst.iam.domain.dto.requests.RequestUserLogin;

public interface BaseAuthenticationRequestsValidator {
    void validateAuthenticationStandard(RequestUserLogin requestUserLogin);}
