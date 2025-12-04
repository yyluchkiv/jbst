package jbst.foundation.domain.events;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;

public record JbstEventRegistrationMagicLink(
        JbstRequestUserRegistrationMagicLink request
) {
}
