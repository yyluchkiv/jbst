package jbst.foundation.sessions;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.events.JbstEventAuthenticationLogin;
import jbst.foundation.domain.events.JbstEventAuthenticationLogout;
import jbst.foundation.domain.events.JbstEventSessionExpired;
import jbst.foundation.domain.events.JbstEventSessionRefreshed;
import jbst.foundation.domain.geo.JbstGeoLocation;
import jbst.foundation.domain.http.requests.JbstUserAgentDetails;
import jbst.foundation.domain.http.requests.JbstUserRequestMetadata;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtRefreshToken;
import jbst.foundation.domain.sessions.JbstSession;
import jbst.foundation.domain.sessions.JbstSessionsExpiredTable;
import jbst.foundation.domain.tuples.Tuple2;
import jbst.foundation.domain.tuples.Tuple3;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutFull;
import jbst.foundation.incidents.domain.authetication.IncidentAuthenticationLogoutMin;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.services.JbstUsersSessionsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static jbst.foundation.domain.http.requests.JbstUserRequestMetadata.processed;
import static jbst.foundation.domain.tuples.TuplePresence.absent;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandom.randomString;
import static jbst.foundation.domain.reflection.JbstReflections.setPrivateFieldOfSuperClass;
import static jbst.foundation.domain.time.JbstTime.getCurrentTimestamp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAbstractSessionRegistryTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstEventsPublisher eventsPublisher() {
            return mock(JbstEventsPublisher.class);
        }

        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstUsersSessionsService userSessionService() {
            return mock(JbstUsersSessionsService.class);
        }

        @Bean
        JbstUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstUsersSessionsRepository.class);
        }

        @Bean
        JbstSessionRegistry sessionRegistry() {
            return new JbstAbstractSessionRegistry(
                    this.eventsPublisher(),
                    this.incidentsPublisher(),
                    this.userSessionService(),
                    this.usersSessionsRepository()
            ) {};
        }
    }

    // Publishers
    private final JbstEventsPublisher eventsPublisher;
    private final JbstIncidentsPublisher incidentsPublisher;
    // Services
    private final JbstUsersSessionsService usersSessionsService;
    // Repositories
    private final JbstUsersSessionsRepository usersSessionsRepository;

    private final JbstSessionRegistry componentUnderTest;

    @BeforeEach
    void beforeEach() throws Exception {
        // Clean sessions to execute a.k.a. integration test -> method "integrationFlow"
        setPrivateFieldOfSuperClass(this.componentUnderTest, "sessions", ConcurrentHashMap.newKeySet(), 1);
        reset(
                this.incidentsPublisher,
                this.eventsPublisher,
                this.usersSessionsService,
                this.usersSessionsRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentsPublisher,
                this.eventsPublisher,
                this.usersSessionsService,
                this.usersSessionsRepository
        );
    }

    private JbstSession authenticateHardcoded(JbstJwtAccessToken accessToken) throws NoSuchFieldException, IllegalAccessException {
        var session = new JbstSession(Username.hardcoded(), accessToken, JbstJwtRefreshToken.random());
        var sessions = ConcurrentHashMap.newKeySet();
        sessions.add(session);
        setPrivateFieldOfSuperClass(this.componentUnderTest, "sessions", sessions, 1);
        return session;
    }

    @Test
    void integrationTest() {
        // Arrange
        var session1 = new JbstSession(Username.of("username1"), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var session2 = new JbstSession(Username.of("username2"), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var session3 = new JbstSession(Username.of("username3"), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var session4 = new JbstSession(Username.of("username4"), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var rndSession = new JbstSession(Username.random(), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var dbUserSession1 = entity(JbstUserSession.class);
        var dbUserSession2 = entity(JbstUserSession.class);
        var dbUserSession3 = entity(JbstUserSession.class);
        var dbUserSession4 = entity(JbstUserSession.class);
        var rndDbUserSession = entity(JbstUserSession.class);
        when(this.usersSessionsRepository.isPresent(session1.accessToken())).thenReturn(present(dbUserSession1));
        when(this.usersSessionsRepository.isPresent(session2.accessToken())).thenReturn(present(dbUserSession2));
        when(this.usersSessionsRepository.isPresent(session3.accessToken())).thenReturn(present(dbUserSession3));
        when(this.usersSessionsRepository.isPresent(session4.accessToken())).thenReturn(present(dbUserSession4));
        when(this.usersSessionsRepository.isPresent(rndSession.accessToken())).thenReturn(present(rndDbUserSession));

        // Iteration #1
        var activeSessionsUsernames1 = this.componentUnderTest.getActiveSessionsUsernamesIdentifiers();
        assertThat(activeSessionsUsernames1).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsAccessTokens()).isEmpty();

        // Iteration #2
        this.componentUnderTest.register(session1);
        this.componentUnderTest.register(session2);
        var activeSessionsUsernames2 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames2).hasSize(2);
        assertThat(this.componentUnderTest.getActiveSessionsAccessTokens()).hasSize(2);
        assertThat(activeSessionsUsernames2).isEqualTo(Set.of(session1.username(), session2.username()));

        // Iteration #3
        this.componentUnderTest.register(session3);
        this.componentUnderTest.logout(rndSession.username(), rndSession.accessToken());
        var activeSessionsUsernames3 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames3).hasSize(3);
        assertThat(this.componentUnderTest.getActiveSessionsAccessTokens()).hasSize(3);
        assertThat(activeSessionsUsernames3).isEqualTo(Set.of(session1.username(), session2.username(), session3.username()));

        // Iteration #4
        this.componentUnderTest.register(session4);
        this.componentUnderTest.logout(session1.username(), session1.accessToken());
        this.componentUnderTest.logout(session2.username(), session2.accessToken());
        this.componentUnderTest.logout(session3.username(), session3.accessToken());
        var activeSessionsUsernames4 = this.componentUnderTest.getActiveSessionsUsernames();
        assertThat(activeSessionsUsernames4).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsAccessTokens()).hasSize(1);
        assertThat(activeSessionsUsernames4).isEqualTo(Set.of(session4.username()));

        // Iteration #5 (cleanup)
        this.componentUnderTest.logout(session4.username(), session4.accessToken());
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).isEmpty();
        assertThat(this.componentUnderTest.getActiveSessionsAccessTokens()).isEmpty();
        verify(this.usersSessionsRepository).isPresent(session1.accessToken());
        verify(this.usersSessionsRepository).isPresent(session2.accessToken());
        verify(this.usersSessionsRepository).isPresent(session3.accessToken());
        verify(this.usersSessionsRepository).isPresent(session4.accessToken());
        verify(this.eventsPublisher).publishAuthenticationLogin(new JbstEventAuthenticationLogin(session1.username()));
        verify(this.eventsPublisher).publishAuthenticationLogin(new JbstEventAuthenticationLogin(session2.username()));
        verify(this.eventsPublisher).publishAuthenticationLogin(new JbstEventAuthenticationLogin(session3.username()));
        verify(this.eventsPublisher).publishAuthenticationLogin(new JbstEventAuthenticationLogin(session4.username()));
        verify(this.eventsPublisher).publishAuthenticationLogout(new JbstEventAuthenticationLogout(session1.username()));
        verify(this.eventsPublisher).publishAuthenticationLogout(new JbstEventAuthenticationLogout(session2.username()));
        verify(this.eventsPublisher).publishAuthenticationLogout(new JbstEventAuthenticationLogout(session3.username()));
        verify(this.eventsPublisher).publishAuthenticationLogout(new JbstEventAuthenticationLogout(session4.username()));
        verify(this.incidentsPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session1.username(), dbUserSession1.metadata()));
        verify(this.incidentsPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session2.username(), dbUserSession2.metadata()));
        verify(this.incidentsPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session3.username(), dbUserSession3.metadata()));
        verify(this.incidentsPublisher).publishAuthenticationLogoutFull(new IncidentAuthenticationLogoutFull(session4.username(), dbUserSession4.metadata()));
        verify(this.usersSessionsRepository).delete(dbUserSession1.id());
        verify(this.usersSessionsRepository).delete(dbUserSession2.id());
        verify(this.usersSessionsRepository).delete(dbUserSession3.id());
        verify(this.usersSessionsRepository).delete(dbUserSession4.id());
    }

    @Test
    void registerTest() {
        // Act
        this.componentUnderTest.register(new JbstSession(Username.hardcoded(), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random()));
        this.componentUnderTest.register(new JbstSession(Username.hardcoded(), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random()));

        var duplicatedAccessToken = JbstJwtAccessToken.random();
        var duplicatedRefreshToken = JbstJwtRefreshToken.random();
        this.componentUnderTest.register(new JbstSession(Username.hardcoded(), duplicatedAccessToken, duplicatedRefreshToken));
        this.componentUnderTest.register(new JbstSession(Username.hardcoded(), duplicatedAccessToken, duplicatedRefreshToken));
        this.componentUnderTest.register(new JbstSession(Username.hardcoded(), duplicatedAccessToken, duplicatedRefreshToken));

        // Assert
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(1);
        verify(this.eventsPublisher, times(3)).publishAuthenticationLogin(new JbstEventAuthenticationLogin(Username.hardcoded()));
    }

    @Test
    void renewTest() {
        // Act
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());

        var duplicatedAccessToken = JbstJwtAccessToken.random();
        var duplicatedRefreshToken = JbstJwtRefreshToken.random();
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), duplicatedAccessToken, duplicatedRefreshToken);
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), duplicatedAccessToken, duplicatedRefreshToken);
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), duplicatedAccessToken, duplicatedRefreshToken);
        this.componentUnderTest.renew(Username.hardcoded(), JbstJwtRefreshToken.random(), duplicatedAccessToken, duplicatedRefreshToken);

        // Assert
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(1);
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).isEqualTo(Set.of("jbst"));
        verify(this.eventsPublisher, times(3)).publishSessionRefreshed(any(JbstEventSessionRefreshed.class));
    }

    @Test
    void logoutDbUserSessionPresentTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var accessToken = JbstJwtAccessToken.random();
        this.authenticateHardcoded(accessToken);
        var dbUserSession = entity(JbstUserSession.class);
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(present(dbUserSession));

        // Act
        this.componentUnderTest.logout(Username.hardcoded(), accessToken);

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
        var eventAC = ArgumentCaptor.forClass(JbstEventAuthenticationLogout.class);
        verify(this.eventsPublisher).publishAuthenticationLogout(eventAC.capture());
        verify(this.eventsPublisher).publishAuthenticationLogout(eventAC.capture());
        var incidentAC = ArgumentCaptor.forClass(IncidentAuthenticationLogoutFull.class);
        verify(this.incidentsPublisher).publishAuthenticationLogoutFull(incidentAC.capture());
        var incident = incidentAC.getValue();
        assertThat(incident.username()).isEqualTo(Username.hardcoded());
        assertThat(incident.userRequestMetadata()).isEqualTo(dbUserSession.metadata());
        verify(this.usersSessionsRepository).delete(dbUserSession.id());
    }

    @Test
    void logoutDbUserSessionNotPresentTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var accessToken = JbstJwtAccessToken.random();
        var session = this.authenticateHardcoded(accessToken);
        when(this.usersSessionsRepository.isPresent(accessToken)).thenReturn(absent());

        // Act
        this.componentUnderTest.logout(Username.hardcoded(), accessToken);

        // Assert
        verify(this.usersSessionsRepository).isPresent(accessToken);
        var eventAC = ArgumentCaptor.forClass(JbstEventAuthenticationLogout.class);
        verify(this.eventsPublisher).publishAuthenticationLogout(eventAC.capture());
        assertThat(eventAC.getValue().username()).isEqualTo(session.username());
        var incidentAC = ArgumentCaptor.forClass(IncidentAuthenticationLogoutMin.class);
        verify(this.incidentsPublisher).publishAuthenticationLogoutMin(incidentAC.capture());
        assertThat(incidentAC.getValue().username()).isEqualTo(Username.hardcoded());
    }

    @Test
    void cleanByExpiredRefreshTokensEnabledTest() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        var username1 = Username.of("username1");
        var username2 = Username.of("username2");
        var username3 = Username.of("username3");
        var session1 = new JbstSession(username1, JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var session2 = new JbstSession(username2, JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        var session3 = new JbstSession(username3, JbstJwtAccessToken.random(), JbstJwtRefreshToken.random());
        Set<JbstSession> sessions = ConcurrentHashMap.newKeySet();
        sessions.add(session1);
        sessions.add(session2);
        sessions.add(session3);
        setPrivateFieldOfSuperClass(this.componentUnderTest, "sessions", sessions, 1);
        var dbUserSession1 = entity(JbstUserSession.class);
        var dbUserSession2 = entity(JbstUserSession.class);
        var dbUserSession3 = entity(JbstUserSession.class);
        var sessionsExpiredTable = new JbstSessionsExpiredTable(
                List.of(
                        new Tuple3<>(Username.hardcoded(), JbstJwtRefreshToken.random(), JbstUserRequestMetadata.random()),
                        new Tuple3<>(username3, session3.refreshToken(), dbUserSession3.metadata())
                ),
                Set.of(dbUserSession1.id(), dbUserSession2.id())
        );
        var usernames = Set.of(username1, username2, username3);
        when(this.usersSessionsService.getExpiredRefreshTokensSessions(usernames)).thenReturn(sessionsExpiredTable);

        // Act
        this.componentUnderTest.cleanByExpiredRefreshTokens(usernames);

        // Assert
        verify(this.usersSessionsService).getExpiredRefreshTokensSessions(usernames);
        assertThat(this.componentUnderTest.getActiveSessionsUsernames()).hasSize(2);
        assertThat(this.componentUnderTest.getActiveSessionsUsernamesIdentifiers()).isEqualTo(Set.of("username1", "username2"));
        var eseCaptor = ArgumentCaptor.forClass(JbstEventSessionExpired.class);
        verify(this.eventsPublisher).publishSessionExpired(eseCaptor.capture());
        var eventSessionExpired = eseCaptor.getValue();
        assertThat(eventSessionExpired.session().username()).isEqualTo(username3);
        assertThat(eventSessionExpired.session().accessToken()).isEqualTo(session3.accessToken());
        assertThat(eventSessionExpired.session().refreshToken()).isEqualTo(session3.refreshToken());
        var seiCaptor = ArgumentCaptor.forClass(IncidentSessionExpired.class);
        verify(this.incidentsPublisher).publishSessionExpired(seiCaptor.capture());
        var sessionExpiredIncident = seiCaptor.getValue();
        assertThat(sessionExpiredIncident.username()).isEqualTo(username3);
        assertThat(sessionExpiredIncident.userRequestMetadata()).isEqualTo(dbUserSession3.metadata());
        verify(this.usersSessionsRepository).delete(Set.of(dbUserSession1.id(), dbUserSession2.id()));
    }

    @Test
    void getSessionsTableTest() {
        // Arrange
        var username = entity(Username.class);
        var requestAccessToken = JbstRequestAccessToken.random();

        Function<Tuple2<JbstUserRequestMetadata, String>, JbstResponseUserSession2> sessionFnc =
                tuple2 -> JbstResponseUserSession2.of(entity(JbstUserSessionId.class), getCurrentTimestamp(), Username.random(), requestAccessToken, new JbstJwtAccessToken(tuple2.b()), tuple2.a());

        var validSession = sessionFnc.apply(new Tuple2<>(processed(JbstGeoLocation.valid(), JbstUserAgentDetails.valid()), requestAccessToken.value()));
        var invalidSession1 = sessionFnc.apply(new Tuple2<>(processed(JbstGeoLocation.invalid(), JbstUserAgentDetails.valid()), randomString()));
        var invalidSession2 = sessionFnc.apply(new Tuple2<>(processed(JbstGeoLocation.valid(), JbstUserAgentDetails.invalid()), randomString()));
        var invalidSession3 = sessionFnc.apply(new Tuple2<>(processed(JbstGeoLocation.invalid(), JbstUserAgentDetails.invalid()), randomString()));

        // userSessions, expectedSessionSize, expectedAnyProblems
        List<Tuple3<List<JbstResponseUserSession2>, Integer, Boolean>> cases = new ArrayList<>();
        cases.add(
                new Tuple3<>(
                        new ArrayList<>(List.of(validSession)),
                        1,
                        false
                )
        );
        cases.add(
                new Tuple3<>(
                        new ArrayList<>(List.of(validSession, invalidSession1)),
                        2,
                        true
                )
        );
        cases.add(
                new Tuple3<>(
                        new ArrayList<>(List.of(validSession, invalidSession1, invalidSession2)),
                        3,
                        true
                )
        );
        cases.add(
                new Tuple3<>(
                        new ArrayList<>(List.of(validSession, invalidSession1, invalidSession2, invalidSession3)),
                        4,
                        true
                )
        );

        // Act
        cases.forEach(item -> {
            // Arrange
            var userSessions = item.a();
            var expectedSessionSize = item.b();
            var expectedAnyProblems = item.c();
            when(this.usersSessionsRepository.getUsersSessionsTable(username, requestAccessToken)).thenReturn(userSessions);

            // Act
            var currentUserDbSessionsTable = this.componentUnderTest.getSessionsTable(username, requestAccessToken);

            // Assert
            verify(this.usersSessionsRepository).getUsersSessionsTable(username, requestAccessToken);
            assertThat(currentUserDbSessionsTable).isNotNull();
            assertThat(currentUserDbSessionsTable.sessions()).hasSize(expectedSessionSize);
            assertThat(currentUserDbSessionsTable.sessions().stream().filter(JbstResponseUserSession2::current).count()).isEqualTo(1);
            assertThat(currentUserDbSessionsTable.sessions().stream().filter(session -> "Current session".equals(session.activity())).count()).isEqualTo(1);
            assertThat(currentUserDbSessionsTable.anyProblem()).isEqualTo(expectedAnyProblems);

            reset(
                    this.usersSessionsRepository
            );
        });
    }
}
