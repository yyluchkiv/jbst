package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.foundation.domain.exceptions.authentication.JbstPasswordResetException;
import jbst.foundation.domain.exceptions.tokens.JbstUserTokenValidationException;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.validators.BaseUsersTokensRequestsValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersTokensRequestsValidator implements BaseUsersTokensRequestsValidator {

    // Repositories
    protected final JbstUsersTokensRepository usersTokensRepository;

    @Override
    public void validateExecuteConfirmEmail(JwtUser user) {
        assertFalseOrThrow(user.emailDetails().isEnabled(), "User email already confirmed");
        assertNonNullOrThrow(user.email(), "User email is missing");
    }

    @Override
    public void validateEmailConfirmationToken(String token) throws JbstUserTokenValidationException {
        this.validateToken(token, UserTokenType.EMAIL_CONFIRMATION);
    }

    @Override
    public void validateExecuteResetPassword(JwtUser user) throws JbstPasswordResetException {
        if (isNull(user)) {
            throw JbstPasswordResetException.userNotFound();
        }
        if (isNull(user.email())) {
            throw JbstPasswordResetException.emailMissing();
        }
        if (!user.emailDetails().isEnabled()) {
            throw JbstPasswordResetException.emailNotConfirmed();
        }
    }

    @Override
    public void validatePasswordReset(RequestUserPasswordReset request) throws JbstUserTokenValidationException {
        request.assertPasswordsOrThrow();
        this.validateToken(request.token(), UserTokenType.PASSWORD_RESET);
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void validateToken(String token, UserTokenType type) throws JbstUserTokenValidationException {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(token);
        if (isNull(userToken)) {
            throw JbstUserTokenValidationException.notFound();
        }
        if (userToken.used()) {
            throw JbstUserTokenValidationException.used();
        }
        if (userToken.isExpired()) {
            throw JbstUserTokenValidationException.expired();
        }
        if (!userToken.type().equals(type)) {
            throw JbstUserTokenValidationException.invalidType();
        }
    }
}
