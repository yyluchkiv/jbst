package jbst.foundation.incidents.services;

import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionExpired;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerStarted;
import jbst.foundation.incidents.clients.JbstIncidentClient;
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
        var incident = JbstIncident.random();

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident);
    }

    @Test
    void onEventIncidentSystemResetServerStartedTest() {
        // Arrange
        var incidentSystemResetServerStarted = JbstIncidentSystemResetServerStarted.hardcoded();

        // Act
        this.componentUnderTest.onEvent(incidentSystemResetServerStarted);

        // Assert
        var incidentAC = ArgumentCaptor.forClass(JbstIncident.class);
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
        var incidentSystemResetServerStarted = JbstIncidentSystemResetServerCompleted.hardcoded();

        // Act
        this.componentUnderTest.onEvent(incidentSystemResetServerStarted);

        // Assert
        var incidentAC = ArgumentCaptor.forClass(JbstIncident.class);
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
        var incident = entity(JbstIncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLoginFailureUsernamePasswordIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLoginFailureUsernameMaskedPasswordIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLogoutMinIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventAuthenticationLogoutFullIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventSessionRefreshedIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventSessionExpiredIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentSessionExpired.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegisterMagicLinkIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentRegistrationMagicLink.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister0IncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentRegistration0.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister0FailureIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentRegistration0Failure.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister1IncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentRegistration1.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }

    @Test
    void onEventRegister1FailureIncidentTest() {
        // Arrange
        var incident = entity(JbstIncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident.getPlainIncident());
    }
}
