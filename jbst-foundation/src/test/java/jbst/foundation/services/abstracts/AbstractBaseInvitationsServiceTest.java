package jbst.foundation.services.abstracts;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.InvitationsRepository;
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

import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AbstractBaseInvitationsServiceTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        InvitationsRepository invitationsRepository() {
            return mock(InvitationsRepository.class);
        }

        @Bean
        AbstractBaseInvitationsService abstractBaseInvitationsService() {
            return new AbstractBaseInvitationsService(
                    this.invitationsRepository(),
                    this.jbstProperties
            ) {};
        }
    }

    private final InvitationsRepository invitationsRepository;
    private final JbstProperties jbstProperties;

    private final AbstractBaseInvitationsService componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.invitationsRepository
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.invitationsRepository
        );
    }

    @Test
    void findByOwnerTest() {
        // Arrange
        var owner = Username.random();

        var invitation1 = ResponseInvitation.random(owner, Username.of("user2"));
        var invitation2 = ResponseInvitation.random(owner, Username.of("user1"));
        var invitation3 = ResponseInvitation.random(owner);
        var invitation4 = ResponseInvitation.random(owner);
        var invitation5 = ResponseInvitation.random(owner, Username.of("user5"));
        var invitation6 = ResponseInvitation.random(owner);

        var invitations = asList(invitation1, invitation2, invitation3, invitation4, invitation5, invitation6);
        when(this.invitationsRepository.findResponseCodesByOwner(owner)).thenReturn(invitations);

        // Act
        var responseInvitations = this.componentUnderTest.findByOwner(owner);

        // Assert
        verify(this.invitationsRepository).findResponseCodesByOwner(owner);
        assertThat(responseInvitations.invitations().stream()
                        .limit(3)
                        .map(ResponseInvitation::value)
                        .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitation3.value(),
                invitation4.value(),
                invitation6.value()
        );
        assertThat(responseInvitations.invitations().stream()
                .skip(3)
                .map(ResponseInvitation::value)
                .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitation1.value(),
                invitation2.value(),
                invitation5.value()
        );
        assertThat(responseInvitations.authorities()).isEqualTo(this.jbstProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities());
    }

    @Test
    void saveTest() {
        // Arrange
        var username = Username.random();
        var request = RequestNewInvitationParams.random();

        // Act
        this.componentUnderTest.save(username, request);

        // Assert
        verify(this.invitationsRepository).saveAs(username, request);
    }

    @Test
    void deleteByIdTest() {
        // Arrange
        var invitationId = InvitationId.random();

        // Act
        this.componentUnderTest.deleteById(invitationId);

        // Assert
        verify(this.invitationsRepository).delete(invitationId);
    }
}
