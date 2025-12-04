package jbst.foundation.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;
import java.util.Set;

public interface JbstUsersSessionsRepository {
    TuplePresence<JbstUserSession> isPresent(JbstUserSessionId userSessionId, Username username);
    TuplePresence<JbstUserSession> isPresent(JbstUserSessionId userSessionId);
    TuplePresence<JbstUserSession> isPresent(JbstJwtAccessToken accessToken);
    TuplePresence<JbstUserSession> isPresent(JbstJwtRefreshToken refreshToken);
    List<JbstResponseUserSession2> getUsersSessionsTable(Username username, JbstRequestAccessToken requestAccessToken);
    JbstResponseSuperadminSessionsTable getSessionsTable(Set<JbstJwtAccessToken> activeAccessTokens, JbstRequestAccessToken requestAccessToken);
    List<JbstUserSession> findByUsernameInAsAny(Set<Username> usernames);
    void enableMetadataRenewCron();
    JbstUserSession enableMetadataRenewManually(JbstUserSessionId sessionId);
    void delete(JbstUserSessionId sessionId);
    long delete(Set<JbstUserSessionId> sessionsIds);
    void deleteByUsernameExceptAccessToken(Username username, JbstRequestAccessToken requestAccessToken);
    void deleteExceptAccessToken(JbstRequestAccessToken requestAccessToken);
    JbstUserSession saveAs(JbstUserSession userSession);
}
