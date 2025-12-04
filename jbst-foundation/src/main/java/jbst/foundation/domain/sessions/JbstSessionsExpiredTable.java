package jbst.foundation.domain.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.tuples.Tuple3;

import java.util.List;
import java.util.Set;

public record JbstSessionsExpiredTable(
        List<Tuple3<Username, JwtRefreshToken, JbstUserRequestMetadata>> expiredSessions,
        Set<JbstUserSessionId> expiredOrInvalidSessionIds
) {
}
