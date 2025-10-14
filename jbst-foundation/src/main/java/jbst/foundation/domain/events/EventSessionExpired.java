package jbst.foundation.domain.events;

import jbst.foundation.domain.sessions.Session;

public record EventSessionExpired(
        Session session
) {
}
