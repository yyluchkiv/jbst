package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.validators.JbstUsersTokensValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.assertFalseOrThrow;
import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstUsersTokensValidator implements JbstUsersTokensValidator {

    // Repositories
    protected final JbstUsersTokensRepository usersTokensRepository;

    @Override
    public void validateExecuteConfirmEmail(JwtUser user) {
        assertFalseOrThrow(user.emailDetails().isEnabled(), "User email already confirmed");
        assertNonNullOrThrow(user.email(), "User email is missing");
    }

    @Override
    public void validateEmailConfirmationToken(String token) throws JbstExceptions.UserTokenValidation {
        this.validateToken(token, JbstUserTokenType.EMAIL_CONFIRMATION);
    }

    @Override
    public void validateExecuteResetPassword(JwtUser user) throws JbstExceptions.PasswordReset {
        if (isNull(user)) {
            throw JbstExceptions.PasswordReset.userNotFound();
        }
        if (isNull(user.email())) {
            throw JbstExceptions.PasswordReset.emailMissing();
        }
        if (!user.emailDetails().isEnabled()) {
            throw JbstExceptions.PasswordReset.emailNotConfirmed();
        }
    }

    @Override
    public void validatePasswordReset(RequestUserPasswordReset request) throws JbstExceptions.UserTokenValidation {
        request.assertPasswordsOrThrow();
        this.validateToken(request.token(), JbstUserTokenType.PASSWORD_RESET);
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void validateToken(String token, JbstUserTokenType type) throws JbstExceptions.UserTokenValidation {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(token);
        if (isNull(userToken)) {
            throw JbstExceptions.UserTokenValidation.notFound();
        }
        if (userToken.used()) {
            throw JbstExceptions.UserTokenValidation.used();
        }
        if (userToken.isExpired()) {
            throw JbstExceptions.UserTokenValidation.expired();
        }
        if (!userToken.type().equals(type)) {
            throw JbstExceptions.UserTokenValidation.invalidType();
        }
    }
}
