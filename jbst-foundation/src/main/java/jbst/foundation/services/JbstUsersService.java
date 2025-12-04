package jbst.foundation.services;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.dto.requests.RequestUserUpdate2;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;

public interface JbstUsersService {
    JbstJwtUser findByEmail(Email email);
    UsernamePasswordCredentials saveOrGetMagicLinkCredentials(JbstMagicLinkUserCredentials credentials);
    void updateUser1(JbstJwtUser user, RequestUserUpdate1 request);
    void updateUser2(JbstJwtUser user, RequestUserUpdate2 request);
    void changePasswordRequired(JbstJwtUser user, RequestUserChangePasswordBasic request);
    void changePassword1(JbstJwtUser user, RequestUserChangePasswordBasic request);
    void resetPassword(RequestUserPasswordReset request);
}
