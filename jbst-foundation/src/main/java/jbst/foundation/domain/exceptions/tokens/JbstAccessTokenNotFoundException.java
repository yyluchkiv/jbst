package jbst.foundation.domain.exceptions.tokens;

public class JbstAccessTokenNotFoundException extends Exception {

    public JbstAccessTokenNotFoundException() {
        super("JWT access token not found");
    }
}
