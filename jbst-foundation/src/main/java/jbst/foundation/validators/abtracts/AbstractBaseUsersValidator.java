package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.repositories.UsersRepository;
import jbst.foundation.validators.BaseUsersValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import jbst.foundation.domain.base.Username;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityAlreadyUsed;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersValidator implements BaseUsersValidator {
    // Repositories
    protected final UsersRepository usersRepository;

    @Override
    public void validateUserUpdateRequest1(Username username, RequestUserUpdate1 request) {
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(request.email());
        if (nonNull(user) && !user.username().equals(username)) {
            throw new IllegalArgumentException(entityAlreadyUsed("Email", request.email().value()));
        }
    }

    @Override
    public void validateUserChangePasswordRequestBasic(RequestUserChangePasswordBasic request) {
        request.assertPasswordsOrThrow();
    }
}
