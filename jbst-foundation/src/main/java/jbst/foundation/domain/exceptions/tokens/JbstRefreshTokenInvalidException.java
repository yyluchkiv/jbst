package jbst.foundation.domain.exceptions.tokens;

public class JbstRefreshTokenInvalidException extends Exception {

    public JbstRefreshTokenInvalidException() {
        super("JWT refresh token is invalid");
    }
}
