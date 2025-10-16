package jbst.foundation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountAccessMethod {
    MAGICLINK("magic link"),
    USERNAME_PASSWORD("username/password"),
    SESSION_TOKEN("session token");

    private final String value;

    public boolean isMagicLink() {
        return MAGICLINK.equals(this);
    }

    public boolean isUsernamePassword() {
        return USERNAME_PASSWORD.equals(this);
    }

    public boolean isSessionToken() {
        return SESSION_TOKEN.equals(this);
    }

    public static AccountAccessMethod getMethod(UserCreationOption userCreationOption) {
        return switch (userCreationOption) {
            case STANDARD -> USERNAME_PASSWORD;
            case MAGICLINK -> MAGICLINK;
        };
    }
}
