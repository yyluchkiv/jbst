package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.databases.mongo.MongoDbUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static jbst.foundation.domain.dto.responses.JbstResponseUserSession2.*;
import static jbst.foundation.domain.tuples.TuplePresence.present;

public interface MongoJbstUsersSessionsRepository extends MongoRepository<MongoDbUserSession, String>, JbstUsersSessionsRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JbstUserSession> isPresent(JbstUserSessionId sessionId, Username username) {
        return this.findByIdAndUsername(sessionId.value(), username)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<JbstUserSession> isPresent(JbstUserSessionId sessionId) {
        return this.findById(sessionId.value())
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<JbstUserSession> isPresent(JbstJwtAccessToken accessToken) {
        return this.findByAccessToken(accessToken)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<JbstUserSession> isPresent(JbstJwtRefreshToken refreshToken) {
        return this.findByRefreshToken(refreshToken)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default List<JbstResponseUserSession2> getUsersSessionsTable(Username username, JbstRequestAccessToken requestAccessToken) {
        return this.findByUsername(username).stream()
                .map(session -> session.responseUserSession2(requestAccessToken))
                .sorted(USERS_SESSIONS)
                .collect(Collectors.toList());
    }

    default JbstResponseSuperadminSessionsTable getSessionsTable(Set<JbstJwtAccessToken> activeAccessTokens, JbstRequestAccessToken requestAccessToken) {
        var sessions = this.findAll();

        List<JbstResponseUserSession2> activeSessions = new ArrayList<>();
        List<JbstResponseUserSession2> inactiveSessions = new ArrayList<>();

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

        return new JbstResponseSuperadminSessionsTable(activeSessions, inactiveSessions);
    }

    default List<JbstUserSession> findByUsernameInAsAny(Set<Username> usernames) {
        return this.findByUsernameIn(usernames).stream()
                .map(MongoDbUserSession::userSession)
                .collect(Collectors.toList());
    }

    default void enableMetadataRenewCron() {
        this.setMetadataRenewCron(true);
    }

    default JbstUserSession enableMetadataRenewManually(JbstUserSessionId sessionId) {
        this.setMetadataRenewManually(sessionId.value(), true);
        return this.isPresent(sessionId).value();
    }

    default void delete(JbstUserSessionId sessionId) {
        this.deleteById(sessionId.value());
    }

    default long delete(Set<JbstUserSessionId> sessionsIds) {
        return this.deleteByIdIn(sessionsIds.stream().map(JbstUserSessionId::value).toList());
    }

    default void deleteByUsernameExceptAccessToken(Username username, JbstRequestAccessToken requestAccessToken) {
        this.deleteByUsernameExceptAccessToken(username, requestAccessToken.getJwtAccessToken());
    }

    default void deleteExceptAccessToken(JbstRequestAccessToken requestAccessToken) {
        this.deleteExceptToken(requestAccessToken.getJwtAccessToken());
    }

    default JbstUserSession saveAs(JbstUserSession userSession) {
        var entity = this.save(new MongoDbUserSession(userSession));
        return entity.userSession();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    Optional<MongoDbUserSession> findByIdAndUsername(String sessionId, Username username);
    Optional<MongoDbUserSession> findByAccessToken(JbstJwtAccessToken accessToken);
    Optional<MongoDbUserSession> findByRefreshToken(JbstJwtRefreshToken refreshToken);
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
    void deleteByUsernameExceptAccessToken(Username username, JbstJwtAccessToken accessToken);

    @Query(value = "{ 'accessToken': { $ne: ?0 } }", delete = true)
    void deleteExceptToken(JbstJwtAccessToken accessToken);
}
