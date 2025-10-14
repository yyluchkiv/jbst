package jbst.iam.validators.abtracts;

import jbst.foundation.domain.exceptions.authentication.JbstPasswordResetException;
import jbst.foundation.domain.exceptions.tokens.UserTokenValidationException;
import jbst.iam.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.validators.BaseUsersTokensRequestsValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersTokensRequestsValidator implements BaseUsersTokensRequestsValidator {

    // Repositories
    protected final UsersTokensRepository usersTokensRepository;

    @Override
    public void validateExecuteConfirmEmail(JwtUser user) {
        assertFalseOrThrow(user.emailDetails().isEnabled(), "User email already confirmed");
        assertNonNullOrThrow(user.email(), "User email is missing");
    }

    @Override
    public void validateEmailConfirmationToken(String token) throws UserTokenValidationException {
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
    public void validatePasswordReset(RequestUserPasswordReset request) throws UserTokenValidationException {
        request.assertPasswordsOrThrow();
        this.validateToken(request.token(), UserTokenType.PASSWORD_RESET);
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected void validateToken(String token, UserTokenType type) throws UserTokenValidationException {
        var userToken = this.usersTokensRepository.findByValueAsAny(token);
        if (isNull(userToken)) {
            throw UserTokenValidationException.notFound();
        }
        if (userToken.used()) {
            throw UserTokenValidationException.used();
        }
        if (userToken.isExpired()) {
            throw UserTokenValidationException.expired();
        }
        if (!userToken.type().equals(type)) {
            throw UserTokenValidationException.invalidType();
        }
    }
}
