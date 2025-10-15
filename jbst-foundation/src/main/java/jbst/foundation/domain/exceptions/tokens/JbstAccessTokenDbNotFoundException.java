package jbst.foundation.domain.exceptions.tokens;

import jbst.foundation.domain.base.Username;

public class JbstAccessTokenDbNotFoundException extends Exception {

    public JbstAccessTokenDbNotFoundException(Username username) {
        super("JWT access token is not present in database. Username: " + username);
    }
}
