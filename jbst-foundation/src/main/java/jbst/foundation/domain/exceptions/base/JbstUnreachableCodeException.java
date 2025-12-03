package jbst.foundation.domain.exceptions.base;

import static jbst.foundation.domain.strings.JbstMessages.contactDevelopmentTeam;

public class JbstUnreachableCodeException extends IllegalArgumentException {

    public JbstUnreachableCodeException() {
        super(contactDevelopmentTeam("Unreachable code"));
    }
}
