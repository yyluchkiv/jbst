package jbst.foundation.domain.dto.responses;

import java.util.List;

public record JbstResponseSuperadminSessionsTable(
        List<JbstResponseUserSession2> activeSessions,
        List<JbstResponseUserSession2> inactiveSessions
) {
}
