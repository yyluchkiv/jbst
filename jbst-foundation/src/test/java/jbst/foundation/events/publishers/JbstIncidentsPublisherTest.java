package jbst.foundation.events.publishers;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstIamIncidentType;
import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import jbst.foundation.domain.properties.configs.security.jwt.IncidentsConfigs;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.authetication.*;
import jbst.foundation.incidents.domain.registration.*;
import jbst.foundation.incidents.domain.session.IncidentSessionExpired;
import jbst.foundation.incidents.domain.session.IncidentSessionRefreshed;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
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

import static jbst.foundation.domain.properties.base.JbstIamIncidentType.*;
import static jbst.foundation.utilities.random.EntityUtility.entity;
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
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishAuthenticationLoginEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogin(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLoginFailureUsernamePasswordDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishAuthenticationLoginFailureUsernamePasswordEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_PASSWORD, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernamePassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernamePassword(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLoginFailureUsernameMaskedPasswordDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishAuthenticationLoginFailureUsernameMaskedPasswordEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGIN_FAILURE_USERNAME_MASKED_PASSWORD, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLoginFailureUsernameMaskedPassword.class);

        // Act
        this.componentUnderTest.publishAuthenticationLoginFailureUsernameMaskedPassword(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLogoutMinDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT_MIN, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishAuthenticationLogoutMinEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT_MIN, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutMin.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutMin(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishAuthenticationLogoutFullDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishAuthenticationLogoutFullEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(AUTHENTICATION_LOGOUT, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentAuthenticationLogoutFull.class);

        // Act
        this.componentUnderTest.publishAuthenticationLogoutFull(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishSessionRefreshedDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_REFRESHED, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishSessionRefreshedEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_REFRESHED, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionRefreshed.class);

        // Act
        this.componentUnderTest.publishSessionRefreshed(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishSessionExpiredDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_EXPIRED, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishSessionExpiredEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(SESSION_EXPIRED, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentSessionExpired.class);

        // Act
        this.componentUnderTest.publishSessionExpired(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistrationMagicLinkTest(boolean enabled) {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER_MAGICLINK, enabled);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistrationMagicLink.class);

        // Act
        this.componentUnderTest.publishRegistrationMagicLink(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistration0Test(boolean enabled) {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER0, enabled);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration0.class);

        // Act
        this.componentUnderTest.publishRegistration0(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void publishRegistration0FailureTest(boolean enabled) {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER0_FAILURE, enabled);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration0Failure.class);

        // Act
        this.componentUnderTest.publishRegistration0Failure(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        if (enabled) {
            verify(this.applicationEventPublisher).publishEvent(incident);
        }
    }

    @Test
    void publishRegistration1DisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishRegistration1EnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1.class);

        // Act
        this.componentUnderTest.publishRegistration1(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    @Test
    void publishRegistration1FailureDisabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1_FAILURE, false);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
    }

    @Test
    void publishRegistration1FailureEnabledTest() {
        // Arrange
        var securityJwtConfigs = randomSecurityJwtConfigs(REGISTER1_FAILURE, true);
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(securityJwtConfigs);
        var incident = entity(IncidentRegistration1Failure.class);

        // Act
        this.componentUnderTest.publishRegistration1Failure(incident);

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        verify(this.applicationEventPublisher).publishEvent(incident);
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private static SecurityJwtConfigs randomSecurityJwtConfigs(JbstIamIncidentType type, boolean enabled) {
        var typesConfigs = Stream.of(JbstIamIncidentType.values())
                .collect(Collectors.toMap(
                        entry -> entry,
                        entry -> type.equals(entry) && enabled
                ));
        return new SecurityJwtConfigs(
                null,
                null,
                null,
                new IncidentsConfigs(
                        typesConfigs
                ),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
