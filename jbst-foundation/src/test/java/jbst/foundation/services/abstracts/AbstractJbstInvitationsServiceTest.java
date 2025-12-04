package jbst.foundation.services.abstracts;

import jbst.foundation.configurations.TestJbstConfigurationPropertiesHardcoded;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.JbstRequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.JbstInvitationsRepository;
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
class AbstractJbstInvitationsServiceTest {

    @Configuration
    @Import({
            TestJbstConfigurationPropertiesHardcoded.class
    })
    @RequiredArgsConstructor(onConstructor = @__(@Autowired))
    static class ContextConfiguration {
        private final JbstProperties jbstProperties;

        @Bean
        JbstInvitationsRepository invitationsRepository() {
            return mock(JbstInvitationsRepository.class);
        }

        @Bean
        AbstractJbstInvitationsService abstractBaseInvitationsService() {
            return new AbstractJbstInvitationsService(
                    this.invitationsRepository(),
                    this.jbstProperties
            ) {};
        }
    }

    private final JbstInvitationsRepository invitationsRepository;
    private final JbstProperties jbstProperties;

    private final AbstractJbstInvitationsService componentUnderTest;

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

        var invitation1 = JbstResponseInvitation.random(owner, Username.of("user2"));
        var invitation2 = JbstResponseInvitation.random(owner, Username.of("user1"));
        var invitation3 = JbstResponseInvitation.random(owner);
        var invitation4 = JbstResponseInvitation.random(owner);
        var invitation5 = JbstResponseInvitation.random(owner, Username.of("user5"));
        var invitation6 = JbstResponseInvitation.random(owner);

        var invitations = asList(invitation1, invitation2, invitation3, invitation4, invitation5, invitation6);
        when(this.invitationsRepository.findResponseCodesByOwner(owner)).thenReturn(invitations);

        // Act
        var responseInvitations = this.componentUnderTest.findByOwner(owner);

        // Assert
        verify(this.invitationsRepository).findResponseCodesByOwner(owner);
        assertThat(responseInvitations.invitations().stream()
                        .limit(3)
                        .map(JbstResponseInvitation::value)
                        .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitation3.value(),
                invitation4.value(),
                invitation6.value()
        );
        assertThat(responseInvitations.invitations().stream()
                .skip(3)
                .map(JbstResponseInvitation::value)
                .collect(Collectors.toSet())
        ).containsExactlyInAnyOrder(
                invitation1.value(),
                invitation2.value(),
                invitation5.value()
        );
        assertThat(responseInvitations.authorities()).isEqualTo(this.jbstProperties.getSecurity().getAuthorities().getAvailableAuthorities());
    }

    @Test
    void saveTest() {
        // Arrange
        var username = Username.random();
        var request = JbstRequestNewInvitationParams.random();

        // Act
        this.componentUnderTest.save(username, request);

        // Assert
        verify(this.invitationsRepository).saveAs(username, request);
    }

    @Test
    void deleteByIdTest() {
        // Arrange
        var invitationId = JbstInvitationId.random();

        // Act
        this.componentUnderTest.deleteById(invitationId);

        // Assert
        verify(this.invitationsRepository).delete(invitationId);
    }
}
