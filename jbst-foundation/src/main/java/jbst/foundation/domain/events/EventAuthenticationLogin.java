package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;

public record EventAuthenticationLogin(
        Username username
) {
}
