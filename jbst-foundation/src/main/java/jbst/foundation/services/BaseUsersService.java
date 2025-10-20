package jbst.foundation.services;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.dto.requests.RequestUserUpdate2;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.security.MagicLinkUserCredentials;

public interface BaseUsersService {
    JwtUser findByEmail(Email email);
    UsernamePasswordCredentials saveOrGetMagicLinkCredentials(MagicLinkUserCredentials credentials);
    void updateUser1(JwtUser user, RequestUserUpdate1 request);
    void updateUser2(JwtUser user, RequestUserUpdate2 request);
    void changePasswordRequired(JwtUser user, RequestUserChangePasswordBasic request);
    void changePassword1(JwtUser user, RequestUserChangePasswordBasic request);
    void resetPassword(RequestUserPasswordReset request);
}
