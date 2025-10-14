package jbst.foundation.domain.events;

import jbst.foundation.domain.base.Username;

public record EventAuthenticationLogout(
        Username username
) {
}
