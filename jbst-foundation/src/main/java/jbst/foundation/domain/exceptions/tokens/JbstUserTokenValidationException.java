package jbst.foundation.domain.exceptions.tokens;

public class JbstUserTokenValidationException extends Exception {

    public JbstUserTokenValidationException(String message) {
        super(message);
    }

    public static JbstUserTokenValidationException notFound() {
        return new JbstUserTokenValidationException("Token not found");
    }

    public static JbstUserTokenValidationException used() {
        return new JbstUserTokenValidationException("Token is used");
    }

    public static JbstUserTokenValidationException expired() {
        return new JbstUserTokenValidationException("Token is expired");
    }

    public static JbstUserTokenValidationException invalidType() {
        return new JbstUserTokenValidationException("Token type is invalid");
    }
}
