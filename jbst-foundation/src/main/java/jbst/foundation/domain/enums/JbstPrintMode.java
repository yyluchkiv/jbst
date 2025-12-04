package jbst.foundation.domain.enums;

@SuppressWarnings("unused")
public enum JbstPrintMode {
    FULL,
    SHORT;

    public boolean isShort() {
        return SHORT == this;
    }
}
