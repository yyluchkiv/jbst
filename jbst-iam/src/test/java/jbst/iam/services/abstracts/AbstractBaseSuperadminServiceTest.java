package jbst.iam.services.abstracts;

import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.jwt.JwtAccessToken;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.iam.repositories.InvitationsRepository;
import jbst.iam.repositories.UsersSessionsRepository;
import jbst.iam.sessions.SessionRegistry;
import jbst.iam.tasks.superadmin.AbstractSuperAdminResetServerTask;
import jbst.iam.tests.stubbers.AbstractMockService;
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
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.EntityUtility.list345;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseSuperadminServiceTest {

    @Configuration
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {

        @Bean
        IncidentPublisher incidentPublisher() {
            return mock(IncidentPublisher.class);
        }

        @Bean
        SessionRegistry sessionRegistry() {
            return mock(SessionRegistry.class);
        }

        @Bean
        InvitationsRepository invitationsRepository() {
            return mock(InvitationsRepository.class);
        }

        @Bean
        UsersSessionsRepository usersSessionsRepository() {
            return mock(UsersSessionsRepository.class);
        }

        @Bean
        AbstractMockService abstractMockService() {
            return mock(AbstractMockService.class);
        }

        @Bean
        AbstractSuperAdminResetServerTask abstractSuperAdminResetServerTask() {
            return new AbstractSuperAdminResetServerTask(
                    this.incidentPublisher()
            ) {
                @Override
                public ResetServerStatus getStatus() {
                    return ResetServerStatus.random();
                }

                @Override
                public void resetOnServer(JwtUser initiator) {
                    abstractMockService().executeInheritedMethod();
                }
            };
        }

        @Bean
        AbstractBaseSuperadminService abstractBaseSuperadminService() {
            return new AbstractBaseSuperadminService(
                    this.incidentPublisher(),
                    this.sessionRegistry(),
                    this.invitationsRepository(),
                    this.usersSessionsRepository(),
                    this.abstractSuperAdminResetServerTask()
            ) {};
        }
    }

    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Sessions
    private final SessionRegistry sessionRegistry;
    // Repositories
    private final InvitationsRepository invitationsRepository;
    private final UsersSessionsRepository usersSessionsRepository;
    // Mocks
    private final AbstractMockService abstractMockService;

    private final AbstractBaseSuperadminService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentPublisher,
                this.sessionRegistry,
                this.invitationsRepository,
                this.usersSessionsRepository,
                this.abstractMockService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentPublisher,
                this.sessionRegistry,
                this.invitationsRepository,
                this.usersSessionsRepository,
                this.abstractMockService
        );
    }

    @Test
    void getResetServerStatusTest() {
        // Act
        var actual = this.componentUnderTest.getResetServerStatus();

        // Assert
        assertThat(actual).isEqualTo(ResetServerStatus.random());
    }

    @Test
    void resetServerByTest() {
        // Arrange
        var user = entity(JwtUser.class);

        // Act
        this.componentUnderTest.resetServerBy(user);

        // Assert
        verify(this.incidentPublisher).publishResetServerStarted(new IncidentSystemResetServerStarted(user.username()));
        verify(this.abstractMockService).executeInheritedMethod();
        verify(this.incidentPublisher).publishResetServerCompleted(new IncidentSystemResetServerCompleted(user.username()));
    }

    @Test
    void findUnusedTest() {
        // Arrange
        var invitations = list345(ResponseInvitation.class);
        when(this.invitationsRepository.findUnused()).thenReturn(invitations);

        // Act
        var unused = this.componentUnderTest.findUnused();

        // Assert
        verify(this.invitationsRepository).findUnused();
        assertThat(unused).isEqualTo(invitations);
    }

    @Test
    void getServerSessionsTest() {
        // Arrange
        var requestAccessToken = RequestAccessToken.random();
        var activeSessions = Set.of(JwtAccessToken.random(), JwtAccessToken.random());
        var serverSessionsTable = entity(ResponseSuperadminSessionsTable.class);

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
