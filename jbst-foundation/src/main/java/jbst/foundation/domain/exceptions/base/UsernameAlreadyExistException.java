package jbst.foundation.domain.exceptions.base;

import jbst.foundation.domain.base.Username;
import lombok.Getter;

@Getter
public class UsernameAlreadyExistException extends Exception {
    private final Username username;

    public UsernameAlreadyExistException(Username username) {
        super();
        this.username = username;
    }
}
