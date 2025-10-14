package jbst.foundation.validators;

import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.base.Username;

public interface BaseUsersValidator {
    void validateUserUpdateRequest1(Username username, RequestUserUpdate1 request);
    void validateUserChangePasswordRequestBasic(RequestUserChangePasswordBasic request);
}
