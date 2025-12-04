package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtUser;

public interface JbstUsersTokensValidator {
    void validateExecuteConfirmEmail(JbstJwtUser user);
    void validateEmailConfirmationToken(String token) throws JbstExceptions.UserTokenValidation;
    void validateExecuteResetPassword(JbstJwtUser user) throws JbstExceptions.PasswordReset;
    void validatePasswordReset(RequestUserPasswordReset request) throws JbstExceptions.UserTokenValidation;
}
