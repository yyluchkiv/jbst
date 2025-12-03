
package jbst.foundation.resources.base;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.dto.requests.RequestNewInvitationParams;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseInvitations;
import jbst.foundation.domain.ids.InvitationId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.services.JbstInvitationsService;
import jbst.foundation.validators.JbstInvitationsValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static jbst.foundation.domain.random.JbstRandomEntities.entity;
import static jbst.foundation.domain.random.JbstRandomEntities.list345;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstInvitationsResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstInvitationsService invitationsService;
    // Validators
    private final JbstInvitationsValidator invitationsValidator;
    // Properties
    private final JbstProperties jbstProperties;

    // Resource
    private final JbstInvitationsResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.invitationsService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.invitationsService
        );
    }

    @Test
    void findAllTest() throws Exception {
        // Arrange
        var owner = Username.random();
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(owner);
        var authorities = this.jbstProperties.getSecurity().getAuthorities().getAvailableAuthorities();
        var invitations = list345(ResponseInvitation.class);
        var responseInvitations = new ResponseInvitations(authorities, invitations);
        when(this.invitationsService.findByOwner(owner)).thenReturn(responseInvitations);

        // Act
        this.mvc.perform(get("/invitations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities", hasSize(5)))
                .andExpect(jsonPath("$.invitations", hasSize(invitations.size())));

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.invitationsService).findByOwner(owner);
    }

    @Test
    void saveTest() throws Exception {
        // Arrange
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(Username.hardcoded());
        var request = RequestNewInvitationParams.hardcoded();

        // Act
        this.mvc.perform(
                post("/invitations")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.invitationsValidator).validateCreateNewInvitation(request);
        verify(this.invitationsService).save(Username.hardcoded(), request);
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Arrange
        var username= entity(Username.class);
        var invitationId = entity(InvitationId.class);
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(username);

        // Act
        this.mvc.perform(delete("/invitations/" + invitationId))
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.invitationsValidator).validateDeleteById(username, invitationId);
        verify(this.invitationsService).deleteById(invitationId);
    }
}
