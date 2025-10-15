package jbst.foundation.domain.exceptions.random;

public class JbstIllegalEnumException extends IllegalArgumentException {

    public JbstIllegalEnumException(Class<?> enumClazz) {
        super("Please check enum: " + enumClazz);
    }
}
