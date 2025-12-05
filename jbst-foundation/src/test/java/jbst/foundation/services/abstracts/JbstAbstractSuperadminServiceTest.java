package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.dto.responses.JbstResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JbstJwtAccessToken;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerStarted;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.JbstAbstractTaskOnResetServer;
import jbst.foundation.tests.stubbers.AbstractMockService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Set;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandomEntities.list345;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAbstractSuperadminServiceTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstSessionRegistry sessionRegistry() {
            return mock(JbstSessionRegistry.class);
        }

        @Bean
        JbstInvitationsRepository invitationsRepository() {
            return mock(JbstInvitationsRepository.class);
        }

        @Bean
        JbstUsersRepository usersRepository() {
            return mock(JbstUsersRepository.class);
        }

        @Bean
        JbstUsersSessionsRepository usersSessionsRepository() {
            return mock(JbstUsersSessionsRepository.class);
        }

        @Bean
        AbstractMockService abstractMockService() {
            return mock(AbstractMockService.class);
        }

        @Bean
        JbstAbstractTaskOnResetServer taskOnResetServer() {
            return new JbstAbstractTaskOnResetServer(
                    this.incidentsPublisher()
            ) {
                @Override
                public JbstSystemResetServerStatus getStatus() {
                    return JbstSystemResetServerStatus.random();
                }

                @Override
                public void resetOnServer(JbstJwtUser initiator) {
                    abstractMockService().executeInheritedMethod();
                }
            };
        }

        @Bean
        JbstAbstractSuperadminService abstractBaseSuperadminService() {
            return new JbstAbstractSuperadminService(
                    this.incidentsPublisher(),
                    this.sessionRegistry(),
                    this.invitationsRepository(),
                    this.usersRepository(),
                    this.usersSessionsRepository(),
                    this.taskOnResetServer()
            ) {};
        }
    }

    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;
    // Sessions
    private final JbstSessionRegistry sessionRegistry;
    // Repositories
    private final JbstInvitationsRepository invitationsRepository;
    private final JbstUsersRepository usersRepository;
    private final JbstUsersSessionsRepository usersSessionsRepository;
    // Mocks
    private final AbstractMockService abstractMockService;

    private final JbstAbstractSuperadminService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentsPublisher,
                this.sessionRegistry,
                this.invitationsRepository,
                this.usersRepository,
                this.usersSessionsRepository,
                this.abstractMockService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentsPublisher,
                this.sessionRegistry,
                this.invitationsRepository,
                this.usersRepository,
                this.usersSessionsRepository,
                this.abstractMockService
        );
    }

    @Test
    void getResetServerStatusTest() {
        // Act
        var actual = this.componentUnderTest.getResetServerStatus();

        // Assert
        assertThat(actual).isEqualTo(JbstSystemResetServerStatus.random());
    }

    @Test
    void resetServerByTest() {
        // Arrange
        var user = entity(JbstJwtUser.class);

        // Act
        this.componentUnderTest.resetServerBy(user);

        // Assert
        verify(this.incidentsPublisher).publishResetServerStarted(new JbstIncidentSystemResetServerStarted(user.username()));
        verify(this.abstractMockService).executeInheritedMethod();
        verify(this.incidentsPublisher).publishResetServerCompleted(new JbstIncidentSystemResetServerCompleted(user.username()));
    }

    @Test
    void findInvitationsUnusedTest() {
        // Arrange
        var invitations = list345(JbstResponseInvitation.class);
        when(this.invitationsRepository.findUnused()).thenReturn(invitations);

        // Act
        var unused = this.componentUnderTest.findInvitationsUnused();

        // Assert
        verify(this.invitationsRepository).findUnused();
        assertThat(unused).isEqualTo(invitations);
    }

    @Test
    void findUsersExcept() {
        // Arrange
        var username = Username.hardcoded();
        when(this.usersRepository.findUsersExcept(username)).thenReturn(JbstUsers.hardcoded());

        // Act
        var users = this.componentUnderTest.findUsersExcept(username);

        // Assert
        verify(this.usersRepository).findUsersExcept(username);
        assertThat(users).isEqualTo(JbstUsers.hardcoded());
    }

    @Test
    void disableUser() {
        // Arrange
        var username = Username.hardcoded();

        // Act
        this.componentUnderTest.disableUser(username);

        // Assert
        verify(this.usersRepository).disable(username);
    }

    @Test
    void getServerSessionsTest() {
        // Arrange
        var requestAccessToken = JbstRequestAccessToken.random();
        var activeSessions = Set.of(JbstJwtAccessToken.random(), JbstJwtAccessToken.random());
        var serverSessionsTable = entity(JbstResponseSuperadminSessionsTable.class);

        when(this.sessionRegistry.getActiveSessionsAccessTokens()).thenReturn(activeSessions);
        when(this.usersSessionsRepository.getSessionsTable(activeSessions, requestAccessToken)).thenReturn(serverSessionsTable);


        // Act
        var actual = this.componentUnderTest.getSessions(requestAccessToken);

        // Assert
        verify(this.sessionRegistry).getActiveSessionsAccessTokens();
        verify(this.usersSessionsRepository).getSessionsTable(activeSessions, requestAccessToken);
        assertThat(actual).isEqualTo(serverSessionsTable);
    }
}
