package jbst.iam.validators.abstracts;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.iam.configurations.TestConfigurationValidators;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.repositories.InvitationsRepository;
import jbst.iam.validators.BaseInvitationsRequestsValidator;
import jbst.iam.validators.abtracts.AbstractBaseInvitationsRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityAccessDenied;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseInvitationsRequestsValidatorTest {

    private static Stream<Arguments> validateCreateNewInvitationTest() {
        return Stream.of(
                Arguments.of(new RequestNewInvitationParams(Set.of(INVITATIONS_READ, "code:send")), "Authorities must contains: [admin, invitations:read, invitations:write, prometheus:read, user]"),
                Arguments.of(new RequestNewInvitationParams(Set.of(INVITATIONS_READ, SUPERADMIN)), "Authorities must contains: [admin, invitations:read, invitations:write, prometheus:read, user]"),
                Arguments.of(new RequestNewInvitationParams(Set.of()), null),
                Arguments.of(new RequestNewInvitationParams(Set.of(INVITATIONS_READ, INVITATIONS_WRITE)), null)
        );
    }

    @Configuration
    @Import({
            TestConfigurationValidators.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final InvitationsRepository invitationsRepository;
        private final JbstProperties jbstProperties;

        @Bean
        BaseInvitationsRequestsValidator baseInvitationsRequestsValidator() {
            return new AbstractBaseInvitationsRequestsValidator(
                    this.invitationsRepository,
                    this.jbstProperties
            ) {};
        }
    }

    private final InvitationsRepository invitationsRepository;

    private final BaseInvitationsRequestsValidator componentUnderTest;

    @ParameterizedTest
    @MethodSource("validateCreateNewInvitationTest")
    void validateCreateNewInvitationTest(RequestNewInvitationParams request, String exceptionMessage) {
        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateCreateNewInvitation(request));

        // Assert
        if (nonNull(exceptionMessage)) {
            assertThat(throwable).isNotNull();
            assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
            assertThat(throwable).hasMessageStartingWith(exceptionMessage);
        } else {
            assertThat(throwable).isNull();
        }
    }

    @Test
    void validateDeleteByIdNotFoundTest() {
        // Arrange
        var username = Username.random();
        var invitationId = InvitationId.random();
        when(this.invitationsRepository.isPresent(invitationId)).thenReturn(TuplePresence.absent());

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateDeleteById(username, invitationId));

        // Assert
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(entityNotFound("Invitation", invitationId.value()));
        verify(this.invitationsRepository).isPresent(invitationId);
    }

    @Test
    void validateDeleteByIdAccessDeniedTest() {
        // Arrange
        var username = Username.random();
        var invitationId = InvitationId.random();
        var invitation = JbstInvitation.random();
        when(this.invitationsRepository.isPresent(invitationId)).thenReturn(TuplePresence.present(invitation));

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.validateDeleteById(username, invitationId));

        // Assert
        assertThat(throwable)
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage(entityAccessDenied("Invitation", invitationId.value()));
        verify(this.invitationsRepository).isPresent(invitationId);
    }

    @Test
    void validateDeleteByIdOkTest() {
        // Arrange
        var invitationId = InvitationId.random();
        var invitation = JbstInvitation.random();
        when(this.invitationsRepository.isPresent(invitationId)).thenReturn(TuplePresence.present(invitation));

        // Act
        this.componentUnderTest.validateDeleteById(invitation.owner(), invitationId);

        // Assert
        verify(this.invitationsRepository).isPresent(invitationId);
    }
}
