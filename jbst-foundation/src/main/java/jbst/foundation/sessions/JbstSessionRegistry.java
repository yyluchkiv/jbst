package jbst.foundation.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.events.EventAuthenticationLogin;
import jbst.foundation.domain.events.EventAuthenticationLogout;
import jbst.foundation.domain.events.EventSessionExpired;
import jbst.foundation.domain.events.EventSessionRefreshed;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtRefreshToken;
import jbst.foundation.domain.sessions.Session;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutFull;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutMin;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.BaseUsersSessionsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstSessionRegistry {
    protected final ConcurrentHashMap.KeySetView<Session, Boolean> sessions = ConcurrentHashMap.newKeySet();

    // Publishers
    protected final SecurityJwtEventsPublisher securityJwtEventsPublisher;
    protected final SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher;
    // Services
    protected final BaseUsersSessionsService baseUsersSessionsService;
    // Repositories
    protected final JbstUsersSessionsRepository usersSessionsRepository;

    public final Set<String> getActiveSessionsUsernamesIdentifiers() {
        return this.sessions.stream()
                .map(session -> session.username().value())
                .collect(Collectors.toSet());
    }

    public final Set<Username> getActiveSessionsUsernames() {
        return this.sessions.stream()
                .map(Session::username)
                .collect(Collectors.toSet());
    }

    public final Set<JwtAccessToken> getActiveSessionsAccessTokens() {
        return this.sessions.stream()
                .map(Session::accessToken)
                .collect(Collectors.toSet());
    }

    @Async
    public void register(Session session) {
        var username = session.username();
        boolean added = this.sessions.add(session);
        if (added) {
            LOGGER.debug(USER_ACTION, username, "Session Registration");
            this.securityJwtEventsPublisher.publishAuthenticationLogin(new EventAuthenticationLogin(username));
        }
    }

    @Async
    public void renew(Username username, JwtRefreshToken oldRefreshToken, JwtAccessToken newAccessToken, JwtRefreshToken newRefreshToken) {
        this.sessions.removeIf(session -> session.refreshToken().equals(oldRefreshToken));
        var newSession = new Session(username, newAccessToken, newRefreshToken);
        var added = this.sessions.add(newSession);
        if (added) {
            LOGGER.debug(USER_ACTION, username, "Session Renew");
            this.securityJwtEventsPublisher.publishSessionRefreshed(new EventSessionRefreshed(newSession));
        }
    }

    @Async
    public void logout(Username username, JwtAccessToken accessToken) {
        LOGGER.debug(USER_ACTION, username, "Session Deletion");
        var removed = this.sessions.removeIf(session -> session.accessToken().equals(accessToken));
        if (removed) {
            this.securityJwtEventsPublisher.publishAuthenticationLogout(new EventAuthenticationLogout(username));

            var sessionTP = this.usersSessionsRepository.isPresent(accessToken);

            if (sessionTP.present()) {
                var session = sessionTP.value();
                this.securityJwtIncidentsPublisher.publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(username, session.metadata()));
                this.usersSessionsRepository.delete(session.id());
            } else {
                this.securityJwtIncidentsPublisher.publishAuthenticationLogoutMin(new IncidentAuthenticationLogoutMin(username));
            }
        }

    }

    // think about migrating to separate service/registry
    public final void cleanByExpiredRefreshTokens(Set<Username> usernames) {
        var sessionsValidatedTuple2 = this.baseUsersSessionsService.getExpiredRefreshTokensSessions(usernames);

        sessionsValidatedTuple2.expiredSessions().forEach(tuple -> {
            var username = tuple.a();
            var refreshToken = tuple.b();
            var metadata = tuple.c();

            LOGGER.debug(USER_ACTION, username, "Session Expiration");
            var sessionOpt = this.sessions.stream()
                    .filter(session -> session.refreshToken().equals(refreshToken))
                    .findFirst();

            if (sessionOpt.isPresent()) {
                var session = sessionOpt.get();
                this.sessions.remove(session);
                this.securityJwtEventsPublisher.publishSessionExpired(new EventSessionExpired(session));
                this.securityJwtIncidentsPublisher.publishSessionExpired(new IncidentSessionExpired(username, metadata));
            }
        });

        var deleted = this.usersSessionsRepository.delete(sessionsValidatedTuple2.expiredOrInvalidSessionIds());
        LOGGER.debug("JWT expired or invalid refresh tokens ids was successfully deleted. Count: {}", deleted);
    }

    public final ResponseUserSessionsTable getSessionsTable(Username username, RequestAccessToken requestAccessToken) {
        return ResponseUserSessionsTable.of(this.usersSessionsRepository.getUsersSessionsTable(username, requestAccessToken));
    }
}
