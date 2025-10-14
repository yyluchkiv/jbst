package jbst.foundation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountAccessMethod {
    USERNAME_PASSWORD("username/password"),
    SECURITY_TOKEN("security token");

    private final String value;
}
