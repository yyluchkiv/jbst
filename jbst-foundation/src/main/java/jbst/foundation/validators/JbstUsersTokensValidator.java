package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtUser;

public interface JbstUsersTokensValidator {
    void validateExecuteConfirmEmail(JwtUser user);
    void validateEmailConfirmationToken(String token) throws JbstExceptions.UserTokenValidation;
    void validateExecuteResetPassword(JwtUser user) throws JbstExceptions.PasswordReset;
    void validatePasswordReset(RequestUserPasswordReset request) throws JbstExceptions.UserTokenValidation;
}
