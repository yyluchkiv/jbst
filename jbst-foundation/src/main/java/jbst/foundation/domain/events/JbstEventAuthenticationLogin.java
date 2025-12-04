package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;

public record JbstEventAuthenticationLogin(
        Username username
) {
}
