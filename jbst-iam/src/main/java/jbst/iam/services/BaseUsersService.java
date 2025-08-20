package jbst.iam.services;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.iam.domain.dto.requests.RequestUserPasswordReset;
import jbst.iam.domain.dto.requests.RequestUserUpdate1;
import jbst.iam.domain.dto.requests.RequestUserUpdate2;
import jbst.iam.domain.enums.UserCreationOption;
import jbst.iam.domain.jwt.JwtUser;

import java.time.ZoneId;

public interface BaseUsersService {
    JwtUser findByEmail(Email email);
    JwtUser safeSave(UserCreationOption creationOption, Email email, ZoneId zoneId);
    void updateUser1(JwtUser user, RequestUserUpdate1 request);
    void updateUser2(JwtUser user, RequestUserUpdate2 request);
    void changePasswordRequired(JwtUser user, RequestUserChangePasswordBasic request);
    void changePassword1(JwtUser user, RequestUserChangePasswordBasic request);
    void resetPassword(RequestUserPasswordReset request);
}
