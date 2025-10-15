package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;

public class JbstRefreshTokenExpiredException extends Exception {

    public JbstRefreshTokenExpiredException(Username username) {
        super("JWT refresh token is expired. Username: " + username);
    }
}
