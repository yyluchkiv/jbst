package jbst.foundation.domain.events;

import jbst.foundation.domain.sessions.JbstSession;

public record JbstEventSessionExpired(
        JbstSession session
) {
}
