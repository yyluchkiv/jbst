package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;

public class JbstRefreshTokenDbNotFoundException extends Exception {

    public JbstRefreshTokenDbNotFoundException(Username username) {
        super("JWT refresh token is not present in database. Username: " + username);
    }
}
