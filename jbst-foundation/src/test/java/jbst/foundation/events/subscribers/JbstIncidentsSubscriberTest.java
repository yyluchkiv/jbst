package jbst.foundation.events.subscribers;

import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
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

import static jbst.foundation.utilities.random.EntityUtility.entity;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstIncidentsSubscriberTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        IncidentClient incidentClient() {
            return mock(IncidentClient.class);
        }

        @Bean
        JbstIncidentsSubscriber incidentsSubscriber() {
            return new JbstIncidentsSubscriber(
                    this.incidentClient()
            );
        }
    }

    // Clients
    private final IncidentClient incidentClient;

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
