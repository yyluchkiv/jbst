package jbst.foundation.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
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
    List<ResponseUserSession2> getUsersSessionsTable(Username username, RequestAccessToken requestAccessToken);
    ResponseSuperadminSessionsTable getSessionsTable(Set<JbstJwtAccessToken> activeAccessTokens, RequestAccessToken requestAccessToken);
    List<JbstUserSession> findByUsernameInAsAny(Set<Username> usernames);
    void enableMetadataRenewCron();
    JbstUserSession enableMetadataRenewManually(JbstUserSessionId sessionId);
    void delete(JbstUserSessionId sessionId);
    long delete(Set<JbstUserSessionId> sessionsIds);
    void deleteByUsernameExceptAccessToken(Username username, RequestAccessToken requestAccessToken);
    void deleteExceptAccessToken(RequestAccessToken requestAccessToken);
    JbstUserSession saveAs(JbstUserSession userSession);
}
