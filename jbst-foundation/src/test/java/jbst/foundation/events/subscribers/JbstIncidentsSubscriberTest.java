package jbst.foundation.events.subscribers;

import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.feigns.clients.JbstIncidentClient;
import jbst.foundation.incidents.services.JbstIncidentsSubscriber;
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

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstIncidentsSubscriberTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentClient incidentClient() {
            return mock(JbstIncidentClient.class);
        }

        @Bean
        JbstIncidentsSubscriber incidentsSubscriber() {
            return new JbstIncidentsSubscriber(
                    this.incidentClient()
            );
        }
    }

    // Clients
    private final JbstIncidentClient incidentClient;

    private final JbstIncidentsSubscriber componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentClient
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentClient
        );
    }

    @Test
    void onEventIncidentTest() {
        // Arrange
        var incident = Incident.random();

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident);
    }

    @Test
    void onEventIncidentSystemResetServerStartedTest() {
        // Arrange
        var incidentSystemResetServerStarted = IncidentSystemResetServerStarted.hardcoded();

        // Act
        this.componentUnderTest.onEvent(incidentSystemResetServerStarted);

        // Assert
        var incidentAC = ArgumentCaptor.forClass(Incident.class);
        verify(this.incidentClient).registerIncident(incidentAC.capture());
        var incident = incidentAC.getValue();
        assertThat(incident.getType()).isEqualTo("Reset Server Started");
        assertThat(incident.getUsername().value()).isEqualTo("jbst");
        assertThat(incident.getAttributes()).hasSize(2);
        assertThat(incident.getAttributes()).containsOnlyKeys("incidentType", "username");
        assertThat(incident.getAttributes()).containsEntry("incidentType", "Reset Server Started");
    }

    @Test
    void onEventIncidentSystemResetServerCompletedTest() {
        // Arrange
        var incidentSystemResetServerStarted = IncidentSystemResetServerCompleted.hardcoded();

        // Act
        this.componentUnderTest.onEvent(incidentSystemResetServerStarted);

        // Assert
        var incidentAC = ArgumentCaptor.forClass(Incident.class);
        verify(this.incidentClient).registerIncident(incidentAC.capture());
        var incident = incidentAC.getValue();
        assertThat(incident.getType()).isEqualTo("Reset Server Completed");
        assertThat(incident.getUsername().value()).isEqualTo("jbst");
        assertThat(incident.getAttributes()).hasSize(2);
        assertThat(incident.getAttributes()).containsOnlyKeys("incidentType", "username");
        assertThat(incident.getAttributes()).containsEntry("incidentType", "Reset Server Completed");
    }

    @Test
    void onEventAuthenticationLoginIncidentTest() {
        // Arrange
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLoginFailureUsernamePasswordIncidentTest() {
        // Arrange
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLoginFailureUsernameMaskedPasswordIncidentTest() {
        // Arrange
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLogoutMinIncidentTest() {
        // Arrange
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLogoutFullIncidentTest() {
        // Arrange
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventSessionRefreshedIncidentTest() {
        // Arrange
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventSessionExpiredIncidentTest() {
        // Arrange
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegisterMagicLinkIncidentTest() {
        // Arrange
        var incident = entity(IncidentRegistrationMagicLink.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister0IncidentTest() {
        // Arrange
        var incident = entity(IncidentRegistration0.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister0FailureIncidentTest() {
        // Arrange
        var incident = entity(IncidentRegistration0Failure.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister1IncidentTest() {
        // Arrange
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister1FailureIncidentTest() {
        // Arrange
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }
}
