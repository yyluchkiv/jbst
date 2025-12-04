package jbst.foundation.domain.events;

import jbst.foundation.domain.dto.requests.RequestUserRegistrationMagicLink;

public record JbstEventRegistrationMagicLink(
        RequestUserRegistrationMagicLink request
) {
}
