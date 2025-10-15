package jbst.foundation.domain.exceptions.tokens;

public class JbstAccessTokenInvalidException extends Exception {

    public JbstAccessTokenInvalidException() {
        super("JWT access token is invalid");
    }
}
