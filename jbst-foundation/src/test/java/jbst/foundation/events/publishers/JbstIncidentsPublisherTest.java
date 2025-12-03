package jbst.foundation.events.publishers;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.enums.JbstIncidentsManagerType;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.properties.base.JbstPropertyRemoteServer;
import jbst.foundation.domain.properties.configs.JbstPropertyIncidentsManager;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static jbst.foundation.domain.enums.JbstSecurityJwtIncident.*;
import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstIncidentsPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Primary
        @Bean
        ApplicationEventPublisher applicationEventPublisher() {
            return mock(ApplicationEventPublisher.class);
        }

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return new JbstIncidentsPublisher(
                    this.applicationEventPublisher(),
                    this.jbstProperties()
            );
        }
    }

    // Spring Publisher
    private final ApplicationEventPublisher applicationEventPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    private final JbstIncidentsPublisher componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.applicationEventPublisher,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.applicationEventPublisher,
                this.jbstProperties
        );
    }

    @Test
    void publishIncidentTest() {
        // Arrange
        var incident = Incident.random();

        // Act
        this.componentUnderTest.publishIncident(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishThrowableIncidentTest() {
        // Arrange
        var incident = new Incident(new Throwable("jbst"));

        // Act
        this.componentUnderTest.publishIncident(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishResetServerStartedTest() {
        // Arrange
        var incident = IncidentSystemResetServerStarted.hardcoded();

        // Act
        this.componentUnderTest.publishResetServerStarted(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishResetServerCompletedTest() {
        // Arrange
        var incident = IncidentSystemResetServerCompleted.hardcoded();

        // Act
        this.componentUnderTest.publishResetServerCompleted(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLoginDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishAuthenticationLoginEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLoginFailureUsernamePasswordDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishAuthenticationLoginFailureUsernamePasswordEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLoginFailureUsernameMaskedPasswordDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishAuthenticationLoginFailureUsernameMaskedPasswordEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLogoutMinDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGOUT_MIN, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishAuthenticationLogoutMinEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGOUT_MIN, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLogoutFullDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGOUT, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishAuthenticationLogoutFullEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(AUTHENTICATION_LOGOUT, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishSessionRefreshedDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(SESSION_REFRESHED, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishSessionRefreshedEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(SESSION_REFRESHED, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishSessionExpiredDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(SESSION_EXPIRED, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishSessionExpiredEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(SESSION_EXPIRED, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistrationMagicLinkTest(boolean enabled) {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER_MAGICLINK, enabled);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistrationMagicLink.class);

        // Act
        this.componentUnderTest.publishRegistrationMagicLink(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistration0Test(boolean enabled) {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER0, enabled);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration0.class);

        // Act
        this.componentUnderTest.publishRegistration0(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistration0FailureTest(boolean enabled) {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER0_FAILURE, enabled);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration0Failure.class);

        // Act
        this.componentUnderTest.publishRegistration0Failure(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @Test
    void publishRegistration1DisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER1, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishRegistration1EnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER1, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishRegistration1FailureDisabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER1_FAILURE, false);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
    }

    @Test
    void publishRegistration1FailureEnabledTest() {
        // Arrange
        var incidentsManager = incidentsManager(REGISTER1_FAILURE, true);
        when(this.jbstProperties.getIncidentsManager()).thenReturn(incidentsManager);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.jbstProperties).getIncidentsManager();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static JbstPropertyIncidentsManager incidentsManager(JbstSecurityJwtIncident type, boolean enabled) {
        var types = Stream.of(JbstSecurityJwtIncident.values())
                .collect(Collectors.toMap(
                        JbstSecurityJwtIncident::name,
                        entry -> type.equals(entry) && enabled
                ));
        return new JbstPropertyIncidentsManager(
                true,
                JbstIncidentsManagerType.hardcoded(),
                JbstPropertyRemoteServer.hardcoded(),
                types
        );
    }
}
