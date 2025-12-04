package jbst.foundation.domain.events;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;

public record JbstEventRegistration1(
        JbstRequestUserRegistration1 requestUserRegistration1
) {
}
