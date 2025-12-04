package jbst.foundation.validators.abtracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate1;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.validators.JbstUsersValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.strings.JbstMessages.entityAlreadyUsed;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstAbstractUsersValidator implements JbstUsersValidator {
    // Repositories
    protected final JbstUsersRepository usersRepository;

    @Override
    public void validateUserUpdateRequest1(Username username, JbstRequestUserUpdate1 request) {
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(request.email());
        if (nonNull(user) && !user.username().equals(username)) {
            throw new IllegalArgumentException(entityAlreadyUsed("Email", request.email().value()));
        }
    }

    @Override
    public void validateUserChangePasswordRequestBasic(JbstRequestUserChangePasswordBasic request) {
        request.assertPasswordsOrThrow();
    }
}
