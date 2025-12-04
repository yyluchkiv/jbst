package jbst.foundation.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.sessions.JbstSession;
import org.springframework.scheduling.annotation.Async;

import java.util.Set;

public interface JbstSessionRegistry {
    Set<String> getActiveSessionsUsernamesIdentifiers();
    Set<Username> getActiveSessionsUsernames();
    Set<JbstJwtAccessToken> getActiveSessionsAccessTokens();

    @Async
    void register(JbstSession session);
    @Async
    void renew(Username username, JbstJwtRefreshToken oldRefreshToken, JbstJwtAccessToken newAccessToken, JbstJwtRefreshToken newRefreshToken);
    @Async
    void logout(Username username, JbstJwtAccessToken accessToken);

    // think about migrating to separate service/registry
    void cleanByExpiredRefreshTokens(Set<Username> usernames);
    ResponseUserSessionsTable getSessionsTable(Username username, RequestAccessToken requestAccessToken);
}
