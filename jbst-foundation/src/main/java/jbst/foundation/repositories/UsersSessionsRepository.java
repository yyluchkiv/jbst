package jbst.foundation.repositories;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;
import java.util.Set;

public interface UsersSessionsRepository {
    TuplePresence<JbstUserSession> isPresent(UserSessionId userSessionId, Username username);
    TuplePresence<JbstUserSession> isPresent(UserSessionId userSessionId);
    TuplePresence<JbstUserSession> isPresent(JwtAccessToken accessToken);
    TuplePresence<JbstUserSession> isPresent(JwtRefreshToken refreshToken);
    List<ResponseUserSession2> getUsersSessionsTable(Username username, RequestAccessToken requestAccessToken);
    ResponseSuperadminSessionsTable getSessionsTable(Set<JwtAccessToken> activeAccessTokens, RequestAccessToken requestAccessToken);
    List<JbstUserSession> findByUsernameInAsAny(Set<Username> usernames);
    void enableMetadataRenewCron();
    JbstUserSession enableMetadataRenewManually(UserSessionId sessionId);
    void delete(UserSessionId sessionId);
    long delete(Set<UserSessionId> sessionsIds);
    void deleteByUsernameExceptAccessToken(Username username, RequestAccessToken requestAccessToken);
    void deleteExceptAccessToken(RequestAccessToken requestAccessToken);
    JbstUserSession saveAs(JbstUserSession userSession);
}
