package jbst.foundation.domain.enums;

public enum JbstJwtTokenStorageMethod {
    COOKIES,
    HEADERS;

    public boolean isCookies() {
        return COOKIES.equals(this);
    }

    public boolean isHeaders() {
        return HEADERS.equals(this);
    }
}
