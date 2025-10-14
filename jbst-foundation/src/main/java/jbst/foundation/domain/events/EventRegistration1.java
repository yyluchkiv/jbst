package jbst.foundation.domain.events;

import jbst.foundation.domain.dto.requests.RequestUserRegistration1;

public record EventRegistration1(
        RequestUserRegistration1 requestUserRegistration1
) {
}
