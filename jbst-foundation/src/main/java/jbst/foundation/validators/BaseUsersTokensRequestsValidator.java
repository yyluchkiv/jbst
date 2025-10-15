package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.exceptions.authentication.JbstPasswordResetException;
import jbst.foundation.domain.exceptions.tokens.JbstUserTokenValidationException;
import jbst.foundation.domain.jwt.JwtUser;

public interface BaseUsersTokensRequestsValidator {
    void validateExecuteConfirmEmail(JwtUser user);
    void validateEmailConfirmationToken(String token) throws JbstUserTokenValidationException;
    void validateExecuteResetPassword(JwtUser user) throws JbstPasswordResetException;
    void validatePasswordReset(RequestUserPasswordReset request) throws JbstUserTokenValidationException;
}
