package jbst.foundation.validators.abstracts;

import jbst.foundation.configurations.TestConfigurationValidators;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.events.JbstEventRegistration0Failure;
import jbst.foundation.domain.events.JbstEventRegistration1Failure;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.strings.JbstMessages;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.domain.registration.JbstIncidentRegistration0Failure;
import jbst.foundation.incidents.domain.registration.JbstIncidentRegistration1Failure;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.validators.JbstRegistrationValidator;
import jbst.foundation.validators.abtracts.JbstAbstractRegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAbstractRegistrationValidatorTest {

    @Configuration
    @Import({
            TestConfigurationValidators.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstInvitationsRepository invitationsRepository;
        private final JbstUsersRepository usersRepository;
        private final JbstEventsPublisher eventsPublisher;
        private final JbstIncidentsPublisher incidentsPublisher;

        @Bean
        JbstRegistrationValidator baseInvitationRequestsValidator() {
            return new JbstAbstractRegistrationValidator(
                    this.eventsPublisher,
                    this.incidentsPublisher,
                    this.invitationsRepository,
                    this.usersRepository
            ) {};
        }
    }

    private final JbstEventsPublisher eventsPublisher;
    private final JbstIncidentsPublisher incidentsPublisher;
    private final JbstInvitationsRepository invitationsRepository;
    private final JbstUsersRepository usersRepository;

    private final JbstRegistrationValidator componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.eventsPublisher,
                this.incidentsPublisher,
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentsPublisher,
                this.eventsPublisher,
                this.invitationsRepository,
                this.usersRepository
        );
    }

    @Test
    void validateRegistrationRequest0UsernameAlreadyUsedTest() {
        // Arrange
        var request = JbstRequestUserRegistration0.hardcoded();
        when(this.usersRepository.existsByUsername(request.username())).thenReturn(true);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateRegistrationRequest0(request));

        // Assert
        var exception = JbstMessages.entityAlreadyUsed("Username", request.username().value());
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.Registration.class)
                .hasMessage(exception);
        verify(this.usersRepository).existsByUsername(request.username());
        verify(this.eventsPublisher).publishRegistration0Failure(
                new JbstEventRegistration0Failure(
                        request.email(),
                        request.username(),
                        exception
                )
        );
        verify(this.incidentsPublisher).publishRegistration0Failure(
                new JbstIncidentRegistration0Failure(
                        request.email(),
                        request.username(),
                        exception
                )
        );
    }

    @Test
    void validateRegistrationRequest0EmailAlreadyUsedTest() {
        // Arrange
        var request = JbstRequestUserRegistration0.hardcoded();
        when(this.usersRepository.existsByUsername(request.username())).thenReturn(false);
        when(this.usersRepository.existsByEmail(request.email())).thenReturn(true);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateRegistrationRequest0(request));

        // Assert
        var exception = JbstMessages.entityAlreadyUsed("Email", request.email().value());
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.Registration.class)
                .hasMessage(exception);
        verify(this.usersRepository).existsByUsername(request.username());
        verify(this.usersRepository).existsByEmail(request.email());
        verify(this.eventsPublisher).publishRegistration0Failure(
                new JbstEventRegistration0Failure(
                        request.email(),
                        request.username(),
                        exception
                )
        );
        verify(this.incidentsPublisher).publishRegistration0Failure(
                new JbstIncidentRegistration0Failure(
                        request.email(),
                        request.username(),
                        exception
                )
        );
    }

    @Test
    void validateRegistrationRequest0UsernameEmailFreeTest() throws JbstExceptions.Registration {
        // Arrange
        var request = JbstRequestUserRegistration0.hardcoded();
        when(this.usersRepository.existsByUsername(request.username())).thenReturn(false);
        when(this.usersRepository.existsByEmail(request.email())).thenReturn(false);

        // Act
        this.componentUnderTest.validateRegistrationRequest0(request);

        // Assert
        verify(this.usersRepository).existsByUsername(request.username());
        verify(this.usersRepository).existsByEmail(request.email());
    }

    @Test
    void validateRegistrationRequest1UsernameAlreadyUsedTest() {
        // Arrange
        var request = JbstRequestUserRegistration1.hardcoded();
        when(this.usersRepository.findByUsernameAsJwtUserOrNull(request.username())).thenReturn(JbstJwtUser.hardcoded());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateRegistrationRequest1(request));

        // Assert
        var exception = JbstMessages.entityAlreadyUsed("Username", request.username().value());
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.Registration.class)
                .hasMessage(exception);
        verify(this.usersRepository).findByUsernameAsJwtUserOrNull(request.username());
        verify(this.eventsPublisher).publishRegistration1Failure(
                JbstEventRegistration1Failure.of(
                        request.username(),
                        request.code(),
                        exception
                )
        );
        verify(this.incidentsPublisher).publishRegistration1Failure(
                JbstIncidentRegistration1Failure.of(
                        request.username(),
                        request.code(),
                        exception
                )
        );
    }

    @Test
    void validateRegistrationRequest1InvitationAlreadyUsedTest() {
        // Arrange
        var request = JbstRequestUserRegistration1.hardcoded();
        var invitation = JbstInvitation.random();
        when(this.usersRepository.findByUsernameAsJwtUserOrNull(request.username())).thenReturn(null);
        when(this.invitationsRepository.findByCodeAsAny(request.code())).thenReturn(invitation);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateRegistrationRequest1(request));

        // Assert
        var exception = JbstMessages.entityAlreadyUsed("Code", invitation.code());
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.Registration.class)
                .hasMessage(exception);
        verify(this.usersRepository).findByUsernameAsJwtUserOrNull(request.username());
        verify(this.invitationsRepository).findByCodeAsAny(request.code());
        verify(this.eventsPublisher).publishRegistration1Failure(
                new JbstEventRegistration1Failure(
                        request.username(),
                        request.code(),
                        invitation.owner(),
                        exception
                )
        );
        verify(this.incidentsPublisher).publishRegistration1Failure(
                new JbstIncidentRegistration1Failure(
                        request.username(),
                        request.code(),
                        invitation.owner(),
                        exception
                )
        );
    }

    @Test
    void validateRegistrationRequest1NoInvitationTest() {
        // Arrange
        var request = JbstRequestUserRegistration1.hardcoded();
        var username = request.username();
        var invitation = request.code();
        when(this.usersRepository.findByUsernameAsJwtUserOrNull(username)).thenReturn(null);
        when(this.invitationsRepository.findByCodeAsAny(invitation)).thenReturn(null);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateRegistrationRequest1(request));

        // Assert
        var exception = JbstMessages.entityNotFound("Code", invitation);
        assertThat(throwable)
                .isInstanceOf(JbstExceptions.Registration.class)
                .hasMessage(exception);
        verify(this.usersRepository).findByUsernameAsJwtUserOrNull(username);
        verify(this.invitationsRepository).findByCodeAsAny(invitation);
        verify(this.eventsPublisher).publishRegistration1Failure(
                JbstEventRegistration1Failure.of(
                        username,
                        invitation,
                        exception
                )
        );
        verify(this.incidentsPublisher).publishRegistration1Failure(
                JbstIncidentRegistration1Failure.of(
                        username,
                        invitation,
                        exception
                )
        );
    }

    @Test
    void validateRegistrationRequest1InvitationPresentTest() throws JbstExceptions.Registration {
        // Arrange
        var request = JbstRequestUserRegistration1.hardcoded();
        when(this.usersRepository.findByUsernameAsJwtUserOrNull(request.username())).thenReturn(null);
        when(this.invitationsRepository.findByCodeAsAny(request.code())).thenReturn(JbstInvitation.randomNoInvited());

        // Act
        this.componentUnderTest.validateRegistrationRequest1(request);

        // Assert
        verify(this.usersRepository).findByUsernameAsJwtUserOrNull(request.username());
        verify(this.invitationsRepository).findByCodeAsAny(request.code());
    }
}
