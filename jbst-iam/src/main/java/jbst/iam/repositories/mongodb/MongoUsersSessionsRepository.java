package jbst.iam.repositories.mongodb;

import jbst.iam.domain.db.UserSession;
import jbst.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.iam.domain.dto.responses.ResponseUserSession2;
import jbst.iam.domain.identifiers.UserSessionId;
import jbst.iam.domain.jwt.RequestAccessToken;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.iam.repositories.UsersSessionsRepository;
import jbst.iam.domain.mongodb.MongoDbUserSession;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.iam.domain.dto.responses.ResponseUserSession2.*;

public interface MongoUsersSessionsRepository extends MongoRepository<MongoDbUserSession, String>, UsersSessionsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<UserSession> isPresent(UserSessionId sessionId, Username username) {
        return this.findByIdAndUsername(sessionId.value(), username)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<UserSession> isPresent(UserSessionId sessionId) {
        return this.findById(sessionId.value())
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<UserSession> isPresent(JwtAccessToken accessToken) {
        return this.findByAccessToken(accessToken)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<UserSession> isPresent(JwtRefreshToken refreshToken) {
        return this.findByRefreshToken(refreshToken)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default List<ResponseUserSession2> getUsersSessionsTable(Username username, RequestAccessToken requestAccessToken) {
        return this.findByUsername(username).stream()
                .map(session -> session.responseUserSession2(requestAccessToken))
                .sorted(USERS_SESSIONS)
                .collect(Collectors.toList());
    }

    default ResponseSuperadminSessionsTable getSessionsTable(Set<JwtAccessToken> activeAccessTokens, RequestAccessToken requestAccessToken) {
        var sessions = this.findAll();

        List<ResponseUserSession2> activeSessions = new ArrayList<>();
        List<ResponseUserSession2> inactiveSessions = new ArrayList<>();

        sessions.forEach(session -> {
            var session2 = session.responseUserSession2(requestAccessToken);
            if (activeAccessTokens.contains(session.getAccessToken())) {
                activeSessions.add(session2);
            } else {
                inactiveSessions.add(session2);
            }
        });

        activeSessions.sort(ACTIVE_SESSIONS_AS_SUPERADMIN);
        inactiveSessions.sort(INACTIVE_SESSIONS_AS_SUPERADMIN);

        return new ResponseSuperadminSessionsTable(activeSessions, inactiveSessions);
    }

    default List<UserSession> findByUsernameInAsAny(Set<Username> usernames) {
        return this.findByUsernameIn(usernames).stream()
                .map(MongoDbUserSession::userSession)
                .collect(Collectors.toList());
    }

    default void enableMetadataRenewCron() {
        this.setMetadataRenewCron(true);
    }

    default UserSession enableMetadataRenewManually(UserSessionId sessionId) {
        this.setMetadataRenewManually(sessionId.value(), true);
        return this.isPresent(sessionId).value();
    }

    default void delete(UserSessionId sessionId) {
        this.deleteById(sessionId.value());
    }

    default long delete(Set<UserSessionId> sessionsIds) {
        return this.deleteByIdIn(sessionsIds.stream().map(UserSessionId::value).toList());
    }

    default void deleteByUsernameExceptAccessToken(Username username, RequestAccessToken requestAccessToken) {
        this.deleteByUsernameExceptAccessToken(username, requestAccessToken.getJwtAccessToken());
    }

    default void deleteExceptAccessToken(RequestAccessToken requestAccessToken) {
        this.deleteExceptToken(requestAccessToken.getJwtAccessToken());
    }

    default UserSession saveAs(UserSession userSession) {
        var entity = this.save(new MongoDbUserSession(userSession));
        return entity.userSession();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    Optional<MongoDbUserSession> findByIdAndUsername(String sessionId, Username username);
    Optional<MongoDbUserSession> findByAccessToken(JwtAccessToken accessToken);
    Optional<MongoDbUserSession> findByRefreshToken(JwtRefreshToken refreshToken);
    List<MongoDbUserSession> findByUsername(Username username);
    List<MongoDbUserSession> findByUsernameIn(Set<Username> usernames);

    long deleteByIdIn(List<String> ids);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Query("{}")
    @Update("{ '$set': { 'metadataRenewCron': ?0 } }")
    void setMetadataRenewCron(boolean flag);

    @Query("{ 'id' : ?0}")
    @Update("{ '$set': { 'metadataRenewManually': ?1 } }")
    void setMetadataRenewManually(String sessionId, boolean flag);

    @Query(value = "{ 'username': { '$in': ?0}}", delete = true)
    void deleteByUsernames(Set<Username> usernames);

    @Query(value = "{ 'username': ?0, 'accessToken': { $ne: ?1 } }", delete = true)
    void deleteByUsernameExceptAccessToken(Username username, JwtAccessToken accessToken);

    @Query(value = "{ 'accessToken': { $ne: ?0 } }", delete = true)
    void deleteExceptToken(JwtAccessToken accessToken);
}
