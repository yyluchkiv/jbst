package jbst.foundation.incidents.services;

import jbst.foundation.domain.enums.JbstIncidentsManagerType;
import jbst.foundation.domain.enums.JbstSecurityJwtIncident;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyRemoteServer;
import jbst.foundation.domain.properties.base.JbstPropertyTelegram;
import jbst.foundation.domain.properties.configs.JbstPropertyIncidentsManager;
import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionExpired;
import jbst.foundation.incidents.domain.session.JbstIncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.JbstIncidentSystemResetServerStarted;
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
        var incident = JbstIncident.random();

        // Act
        this.componentUnderTest.publishIncident(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishThrowableIncidentTest() {
        // Arrange
        var incident = new JbstIncident(new Throwable("jbst"));

        // Act
        this.componentUnderTest.publishIncident(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishResetServerStartedTest() {
        // Arrange
        var incident = JbstIncidentSystemResetServerStarted.fixed();

        // Act
        this.componentUnderTest.publishResetServerStarted(incident);

        // Assert
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishResetServerCompletedTest() {
        // Arrange
        var incident = JbstIncidentSystemResetServerCompleted.fixed();

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
        var incident = entity(JbstIncidentAuthenticationLogin.class);

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
        var incident = entity(JbstIncidentAuthenticationLogin.class);

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
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernamePassword.class);

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
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernamePassword.class);

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
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

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
        var incident = entity(JbstIncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

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
        var incident = entity(JbstIncidentAuthenticationLogoutMin.class);

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
        var incident = entity(JbstIncidentAuthenticationLogoutMin.class);

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
        var incident = entity(JbstIncidentAuthenticationLogoutFull.class);

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
        var incident = entity(JbstIncidentAuthenticationLogoutFull.class);

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
        var incident = entity(JbstIncidentSessionRefreshed.class);

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
        var incident = entity(JbstIncidentSessionRefreshed.class);

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
        var incident = entity(JbstIncidentSessionExpired.class);

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
        var incident = entity(JbstIncidentSessionExpired.class);

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
        var incident = entity(JbstIncidentRegistrationMagicLink.class);

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
        var incident = entity(JbstIncidentRegistration0.class);

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
        var incident = entity(JbstIncidentRegistration0Failure.class);

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
        var incident = entity(JbstIncidentRegistration1.class);

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
        var incident = entity(JbstIncidentRegistration1.class);

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
        var incident = entity(JbstIncidentRegistration1Failure.class);

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
        var incident = entity(JbstIncidentRegistration1Failure.class);

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
                JbstIncidentsManagerType.fixed(),
                JbstPropertyRemoteServer.fixed(),
                JbstPropertyTelegram.fixed(),
                types
        );
    }
}
