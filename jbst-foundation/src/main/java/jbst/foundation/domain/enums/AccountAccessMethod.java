package jbst.foundation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountAccessMethod {
    USERNAME_PASSWORD("username/password"),
    SESSION_TOKEN("session token");

    private final String value;
}
