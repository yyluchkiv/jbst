package jbst.foundation.domain.events;

import jbst.foundation.domain.sessions.JbstSession;

public record JbstEventSessionRefreshed(
        JbstSession session
) {
}
