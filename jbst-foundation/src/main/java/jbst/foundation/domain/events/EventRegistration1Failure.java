package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;

public record EventRegistration1Failure(
        Username username,
        String code,
        Username invitationOwner,
        String exception
) {
    public static EventRegistration1Failure of(
            Username username,
            String code,
            String exception
    ) {
        return new EventRegistration1Failure(
                username,
                code,
                Username.dash(),
                exception
        );
    }
}
