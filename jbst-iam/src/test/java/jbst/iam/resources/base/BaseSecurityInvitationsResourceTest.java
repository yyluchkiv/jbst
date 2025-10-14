
package jbst.iam.resources.base;

import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.domain.dto.requests.RequestNewInvitationParams;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.dto.responses.ResponseInvitations;
import jbst.foundation.domain.ids.InvitationId;
import jbst.iam.services.BaseInvitationsService;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.iam.validators.BaseInvitationsRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.EntityUtility.list345;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSecurityInvitationsResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseInvitationsService baseInvitationsService;
    // Validators
    private final BaseInvitationsRequestsValidator baseInvitationsRequestsValidator;
    // Properties
    private final JbstProperties jbstProperties;

    // Resource
    private final BaseSecurityInvitationsResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.baseInvitationsService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.baseInvitationsService
        );
    }

    @Test
    void findAllTest() throws Exception {
        // Arrange
        var owner = Username.random();
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(owner);
        var authorities = this.jbstProperties.getSecurityJwtConfigs().getAuthoritiesConfigs().getAvailableAuthorities();
        var invitations = list345(ResponseInvitation.class);
        var responseInvitations = new ResponseInvitations(authorities, invitations);
        when(this.baseInvitationsService.findByOwner(owner)).thenReturn(responseInvitations);

        // Act
        this.mvc.perform(get("/invitations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities", hasSize(5)))
                .andExpect(jsonPath("$.invitations", hasSize(invitations.size())));

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.baseInvitationsService).findByOwner(owner);
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
        verify(this.baseInvitationsRequestsValidator).validateCreateNewInvitation(request);
        verify(this.baseInvitationsService).save(Username.hardcoded(), request);
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
        verify(this.baseInvitationsRequestsValidator).validateDeleteById(username, invitationId);
        verify(this.baseInvitationsService).deleteById(invitationId);
    }
}
