package jbst.iam.utils;

import jbst.foundation.domain.jwt.JwtUser;

public interface SecurityPrincipalUtils {
    JwtUser getAuthenticatedJwtUser();
    String getAuthenticatedUsername();
    String getAuthenticatedUsernameOrUnexpected();
}
