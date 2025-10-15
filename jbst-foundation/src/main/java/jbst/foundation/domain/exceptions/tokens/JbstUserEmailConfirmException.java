package jbst.foundation.domain.exceptions.tokens;

public class JbstUserEmailConfirmException extends Exception {

    public JbstUserEmailConfirmException(String message) {
        super(message);
    }

    public static JbstUserEmailConfirmException tokenNotFound() {
        return new JbstUserEmailConfirmException("Token not found");
    }

}
