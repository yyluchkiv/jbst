package jbst.iam.services;

import jbst.foundation.domain.base.Email;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.*;
import jbst.iam.domain.jwt.JwtUser;

public interface BaseUsersService {
    JwtUser findByEmail(Email email);
    JwtUser safeCreateMagicLinkUser(Email email, RequestMagicLinkToken request);
    void updateUser1(JwtUser user, RequestUserUpdate1 request);
    void updateUser2(JwtUser user, RequestUserUpdate2 request);
    void changePasswordRequired(JwtUser user, RequestUserChangePasswordBasic request);
    void changePassword1(JwtUser user, RequestUserChangePasswordBasic request);
    void resetPassword(RequestUserPasswordReset request);
}
