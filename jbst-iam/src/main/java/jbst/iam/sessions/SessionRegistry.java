package jbst.iam.sessions;

import jbst.iam.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.iam.domain.jwt.RequestAccessToken;
import jbst.iam.domain.sessions.Session;
import org.springframework.scheduling.annotation.Async;
import jbst.foundation.domain.base.Username;

import java.util.Set;

public interface SessionRegistry {
    Set<String> getActiveSessionsUsernamesIdentifiers();
    Set<Username> getActiveSessionsUsernames();
    Set<JwtAccessToken> getActiveSessionsAccessTokens();

    @Async
    void register(Session session);
    @Async
    void renew(Username username, JwtRefreshToken oldRefreshToken, JwtAccessToken newAccessToken, JwtRefreshToken newRefreshToken);
    @Async
    void logout(Username username, JwtAccessToken accessToken);

    // think about migrating to separate service/registry
    void cleanByExpiredRefreshTokens(Set<Username> usernames);
    ResponseUserSessionsTable getSessionsTable(Username username, RequestAccessToken requestAccessToken);
}
