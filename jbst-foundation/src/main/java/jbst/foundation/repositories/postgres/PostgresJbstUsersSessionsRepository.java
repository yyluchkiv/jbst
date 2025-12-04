package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUserSession;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static jbst.foundation.domain.dto.responses.ResponseUserSession2.*;
import static jbst.foundation.domain.tuples.TuplePresence.present;

@SuppressWarnings("JpaQlInspection")
public interface PostgresJbstUsersSessionsRepository extends JpaRepository<PostgresDbUserSession, String>, JbstUsersSessionsRepository {
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

    default TuplePresence<JbstUserSession> isPresent(JwtAccessToken accessToken) {
        return this.findByAccessToken(accessToken)
                .map(entity -> present(entity.userSession()))
                .orElseGet(TuplePresence::absent);
    }

    default TuplePresence<JbstUserSession> isPresent(JwtRefreshToken refreshToken) {
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

    default List<JbstUserSession> findByUsernameInAsAny(Set<Username> usernames) {
        return this.findByUsernameIn(usernames).stream()
                .map(PostgresDbUserSession::userSession)
                .collect(Collectors.toList());
    }

    @Transactional
    default void enableMetadataRenewCron() {
        this.setMetadataRenewCron(true);
    }

    @Transactional
    default JbstUserSession enableMetadataRenewManually(JbstUserSessionId sessionId) {
        this.setMetadataRenewManually(sessionId.value(), true);
        return this.isPresent(sessionId).value();
    }

    default void delete(JbstUserSessionId sessionId) {
        this.deleteById(sessionId.value());
    }

    @Transactional
    default long delete(Set<JbstUserSessionId> sessionsIds) {
        return this.deleteByIdIn(sessionsIds.stream().map(JbstUserSessionId::value).toList());
    }

    @Transactional
    default void deleteByUsernameExceptAccessToken(Username username, RequestAccessToken requestAccessToken) {
        this.deleteByUsernameExceptAccessToken(username, requestAccessToken.getJwtAccessToken());
    }

    @Transactional
    default void deleteExceptAccessToken(RequestAccessToken requestAccessToken) {
        this.deleteExceptToken(requestAccessToken.getJwtAccessToken());
    }

    default JbstUserSession saveAs(JbstUserSession userSession) {
        var entity = this.save(new PostgresDbUserSession(userSession));
        return entity.userSession();
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    Optional<PostgresDbUserSession> findByIdAndUsername(String sessionId, Username username);
    Optional<PostgresDbUserSession> findByAccessToken(JwtAccessToken accessToken);
    Optional<PostgresDbUserSession> findByRefreshToken(JwtRefreshToken refreshToken);
    List<PostgresDbUserSession> findByUsername(Username username);
    List<PostgresDbUserSession> findByUsernameIn(Set<Username> usernames);

    @Transactional
    long deleteByIdIn(List<String> ids);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Transactional
    @Modifying
    @Query(value = "UPDATE PostgresDbUserSession s SET s.metadataRenewCron = :flag")
    void setMetadataRenewCron(@Param("flag") boolean flag);

    @Transactional
    @Modifying
    @Query(value = "UPDATE PostgresDbUserSession s SET s.metadataRenewManually = :flag WHERE s.id = :sessionId")
    void setMetadataRenewManually(@Param("sessionId") String sessionId, @Param("flag") boolean flag);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PostgresDbUserSession s WHERE s.username IN :usernames")
    void deleteByUsernames(@Param("usernames") Set<Username> usernames);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PostgresDbUserSession s WHERE s.username = :username AND s.accessToken != :accessToken")
    void deleteByUsernameExceptAccessToken(@Param("username") Username username, @Param("accessToken") JwtAccessToken accessToken);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM PostgresDbUserSession s WHERE s.accessToken != :accessToken")
    void deleteExceptToken(@Param("accessToken") JwtAccessToken accessToken);
}
