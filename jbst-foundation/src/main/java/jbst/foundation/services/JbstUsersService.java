package jbst.foundation.services;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.JbstRequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.JbstRequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate1;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate2;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;

import java.util.List;

public interface JbstUsersService {
    void initUsers();
    long initUsers(List<JbstPropertyUserOnInit> usersOnInit);
    JbstJwtUser findByEmail(Email email);
    UsernamePasswordCredentials saveOrGetMagicLinkCredentials(JbstMagicLinkUserCredentials credentials);
    void updateUser1(JbstJwtUser user, JbstRequestUserUpdate1 request);
    void updateUser2(JbstJwtUser user, JbstRequestUserUpdate2 request);
    void changePasswordRequired(JbstJwtUser user, JbstRequestUserChangePasswordBasic request);
    void changePassword1(JbstJwtUser user, JbstRequestUserChangePasswordBasic request);
    void resetPassword(JbstRequestUserPasswordReset request);
}
