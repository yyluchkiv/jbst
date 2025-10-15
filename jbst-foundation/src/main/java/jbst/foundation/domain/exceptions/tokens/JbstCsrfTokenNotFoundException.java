package jbst.foundation.domain.exceptions.tokens;

public class JbstCsrfTokenNotFoundException extends Exception {

    public JbstCsrfTokenNotFoundException() {
        super("Csrf token not found");
    }
}
