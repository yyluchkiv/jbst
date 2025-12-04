package jbst.foundation.validators;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate1;

public interface JbstUsersValidator {
    void validateUserUpdateRequest1(Username username, JbstRequestUserUpdate1 request);
    void validateUserChangePasswordRequestBasic(JbstRequestUserChangePasswordBasic request);
}
