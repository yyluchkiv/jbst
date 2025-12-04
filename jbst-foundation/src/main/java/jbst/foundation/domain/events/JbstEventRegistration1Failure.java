package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;

public record JbstEventRegistration1Failure(
        Username username,
        String code,
        Username invitationOwner,
        String exception
) {
    public static JbstEventRegistration1Failure of(
            Username username,
            String code,
            String exception
    ) {
        return new JbstEventRegistration1Failure(
                username,
                code,
                Username.dash(),
                exception
        );
    }
}
