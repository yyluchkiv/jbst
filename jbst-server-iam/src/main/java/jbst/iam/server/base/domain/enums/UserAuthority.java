package jbst.iam.server.base.domain.enums;

import jbst.foundation.domain.base.AbstractAuthority;

public enum UserAuthority implements AbstractAuthority {
    USER("user"),
    ADMIN("admin");

    private final String value;

    UserAuthority(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
