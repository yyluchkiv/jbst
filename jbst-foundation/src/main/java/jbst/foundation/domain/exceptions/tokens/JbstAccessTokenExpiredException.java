package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;

public class JbstAccessTokenExpiredException extends Exception {

    public JbstAccessTokenExpiredException(Username username) {
        super("JWT access token is expired. Username: " + username);
    }
}
