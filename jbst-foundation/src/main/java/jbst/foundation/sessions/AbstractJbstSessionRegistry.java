package jbst.foundation.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.events.JbstEventAuthenticationLogin;
import jbst.foundation.domain.events.JbstEventAuthenticationLogout;
import jbst.foundation.domain.events.JbstEventSessionExpired;
import jbst.foundation.domain.events.JbstEventSessionRefreshed;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.sessions.JbstSession;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutFull;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutMin;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.JbstUsersSessionsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Logs.USER_ACTION;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJbstSessionRegistry implements JbstSessionRegistry {
    protected final ConcurrentHashMap.KeySetView<JbstSession, Boolean> sessions = ConcurrentHashMap.newKeySet();

    // Publishers
    protected final JbstEventsPublisher eventsPublisher;
    protected final JbstIncidentsPublisher incidentsPublisher;
    // Services
    protected final JbstUsersSessionsService usersSessionsService;
    // Repositories
    protected final JbstUsersSessionsRepository usersSessionsRepository;

    @Override
    public Set<String> getActiveSessionsUsernamesIdentifiers() {
        return this.sessions.stream()
                .map(session -> session.username().value())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Username> getActiveSessionsUsernames() {
        return this.sessions.stream()
                .map(JbstSession::username)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<JbstJwtAccessToken> getActiveSessionsAccessTokens() {
        return this.sessions.stream()
                .map(JbstSession::accessToken)
                .collect(Collectors.toSet());
    }

    @Override
    public void register(JbstSession session) {
        var username = session.username();
        boolean added = this.sessions.add(session);
        if (added) {
            LOGGER.debug(USER_ACTION, username, "Session Registration");
            this.eventsPublisher.publishAuthenticationLogin(new JbstEventAuthenticationLogin(username));
        }
    }

    @Override
    public void renew(Username username, JbstJwtRefreshToken oldRefreshToken, JbstJwtAccessToken newAccessToken, JbstJwtRefreshToken newRefreshToken) {
        this.sessions.removeIf(session -> session.refreshToken().equals(oldRefreshToken));
        var newSession = new JbstSession(username, newAccessToken, newRefreshToken);
        var added = this.sessions.add(newSession);
        if (added) {
            LOGGER.debug(USER_ACTION, username, "Session Renew");
            this.eventsPublisher.publishSessionRefreshed(new JbstEventSessionRefreshed(newSession));
        }
    }

    @Override
    public void logout(Username username, JbstJwtAccessToken accessToken) {
        LOGGER.debug(USER_ACTION, username, "Session Deletion");
        var removed = this.sessions.removeIf(session -> session.accessToken().equals(accessToken));
        if (removed) {
            this.eventsPublisher.publishAuthenticationLogout(new JbstEventAuthenticationLogout(username));

            var sessionTP = this.usersSessionsRepository.isPresent(accessToken);

            if (sessionTP.present()) {
                var session = sessionTP.value();
                this.incidentsPublisher.publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(username, session.metadata()));
                this.usersSessionsRepository.delete(session.id());
            } else {
                this.incidentsPublisher.publishAuthenticationLogoutMin(new IncidentAuthenticationLogoutMin(username));
            }
        }

    }

    @Override
    public void cleanByExpiredRefreshTokens(Set<Username> usernames) {
        var sessionsValidatedTuple2 = this.usersSessionsService.getExpiredRefreshTokensSessions(usernames);

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
                this.eventsPublisher.publishSessionExpired(new JbstEventSessionExpired(session));
                this.incidentsPublisher.publishSessionExpired(new IncidentSessionExpired(username, metadata));
            }
        });

        var deleted = this.usersSessionsRepository.delete(sessionsValidatedTuple2.expiredOrInvalidSessionIds());
        LOGGER.debug("JWT expired or invalid refresh tokens ids was successfully deleted. Count: {}", deleted);
    }

    @Override
    public ResponseUserSessionsTable getSessionsTable(Username username, RequestAccessToken requestAccessToken) {
        return ResponseUserSessionsTable.of(this.usersSessionsRepository.getUsersSessionsTable(username, requestAccessToken));
    }
}
