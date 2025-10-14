package jbst.iam.repositories;

import jbst.iam.domain.db.UserSession;
import jbst.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.iam.domain.dto.responses.ResponseUserSession2;
import jbst.iam.domain.identifiers.UserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.iam.domain.jwt.RequestAccessToken;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;

import java.util.List;
import java.util.Set;

public interface UsersSessionsRepository {
    TuplePresence<UserSession> isPresent(UserSessionId userSessionId, Username username);
    TuplePresence<UserSession> isPresent(UserSessionId userSessionId);
    TuplePresence<UserSession> isPresent(JwtAccessToken accessToken);
    TuplePresence<UserSession> isPresent(JwtRefreshToken refreshToken);
    List<ResponseUserSession2> getUsersSessionsTable(Username username, RequestAccessToken requestAccessToken);
    ResponseSuperadminSessionsTable getSessionsTable(Set<JwtAccessToken> activeAccessTokens, RequestAccessToken requestAccessToken);
    List<UserSession> findByUsernameInAsAny(Set<Username> usernames);
    void enableMetadataRenewCron();
    UserSession enableMetadataRenewManually(UserSessionId sessionId);
    void delete(UserSessionId sessionId);
    long delete(Set<UserSessionId> sessionsIds);
    void deleteByUsernameExceptAccessToken(Username username, RequestAccessToken requestAccessToken);
    void deleteExceptAccessToken(RequestAccessToken requestAccessToken);
    UserSession saveAs(UserSession userSession);
}
