package jbst.foundation.domain.exceptions.tokens;

public class JbstRefreshTokenNotFoundException extends Exception {

    public JbstRefreshTokenNotFoundException() {
        super("JWT refresh token not found");
    }
}
