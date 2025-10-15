package jbst.foundation.domain.exceptions.base;

import jbst.foundation.domain.base.Username;
import lombok.Getter;

@Getter
public class JbstUsernameAlreadyExistException extends Exception {
    private final Username username;

    public JbstUsernameAlreadyExistException(Username username) {
        super();
        this.username = username;
    }
}
