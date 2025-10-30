package jbst.foundation.resources.base;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.RequestAccessToken;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import jbst.foundation.services.JbstSuperadminService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.EntityUtility.list345;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstSuperadminResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstSuperadminService superadminService;
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;

    // Resource
    private final JbstSuperadminResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.superadminService,
                this.usersSessionsService,
                this.tokensProvider
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.superadminService,
                this.usersSessionsService,
                this.tokensProvider
        );
    }

    @Test
    void getResetServerStatusTest() throws Exception {
        // Arrange
        when(this.superadminService.getResetServerStatus()).thenReturn(ResetServerStatus.random());

        // Act
        this.mvc.perform(get("/superadmin/server/reset/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").doesNotExist())
                .andExpect(jsonPath("$.stage").doesNotExist())
                .andExpect(jsonPath("$.stagesCount").doesNotExist())
                .andExpect(jsonPath("$.percentage").exists())
                .andExpect(jsonPath("$.description").exists());

        // Assert
        verify(this.superadminService).getResetServerStatus();
    }

    @Test
    void resetServerTest() throws Exception {
        // Arrange
        var user = entity(JwtUser.class);
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(user);

        // Act
        this.mvc.perform(post("/superadmin/server/reset"))
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.superadminService).resetServerBy(user);
    }

    @Test
    void getUnusedInvitationsTest() throws Exception {
        // Arrange
        var codes = list345(ResponseInvitation.class);
        when(this.superadminService.findInvitationsUnused()).thenReturn(codes);

        // Act
        this.mvc.perform(get("/superadmin/invitations/unused"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(codes.size())))
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].owner", notNullValue()))
                .andExpect(jsonPath("$.[0].authorities", notNullValue()))
                .andExpect(jsonPath("$.[0].value", notNullValue()))
                .andExpect(jsonPath("$.[0].invited", notNullValue()));

        // Assert
        verify(this.superadminService).findInvitationsUnused();
    }

    @Test
    void findUsersExcept() throws Exception {
        // Arrange
        var username = Username.hardcoded();
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(username);
        when(this.superadminService.findUsersExcept(username)).thenReturn(JbstUsers.hardcoded());

        // Act
        this.mvc.perform(get("/superadmin/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.values.length()").value(1))
                .andExpect(jsonPath("$.values[0].length()").value(8))
                .andExpect(jsonPath("$.values[0].id").exists())
                .andExpect(jsonPath("$.values[0].creationOption").exists())
                .andExpect(jsonPath("$.values[0].username").exists())
                .andExpect(jsonPath("$.values[0].zoneId").exists())
                .andExpect(jsonPath("$.values[0].authorities").exists())
                .andExpect(jsonPath("$.values[0].email").exists())
                .andExpect(jsonPath("$.values[0].name").exists())
                .andExpect(jsonPath("$.values[0].enabled").exists());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.superadminService).findUsersExcept(username);
    }

    @Test
    void disabledUser() throws Exception {
        // Arrange
        var username = Username.hardcoded();

        // Act
        this.mvc.perform(post("/superadmin/users/" + username + "/disable"))
                .andExpect(status().isOk());

        // Assert
        verify(this.superadminService).disableUser(username);
    }

    @Test
    void getSessionsTest() throws Exception {
        // Arrange
        var sessionsTable = new ResponseSuperadminSessionsTable(
                list345(ResponseUserSession2.class),
                list345(ResponseUserSession2.class)
        );
        var requestAccessToken = RequestAccessToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.superadminService.getSessions(requestAccessToken)).thenReturn(sessionsTable);

        // Act
        this.mvc.perform(get("/superadmin/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeSessions", hasSize(sessionsTable.activeSessions().size())))
                .andExpect(jsonPath("$.activeSessions.[0].id", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].who", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].current", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].activity", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].exception", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].ipAddr", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].countryFlag", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].where", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].browser", notNullValue()))
                .andExpect(jsonPath("$.activeSessions.[0].what", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions", hasSize(sessionsTable.inactiveSessions().size())))
                .andExpect(jsonPath("$.inactiveSessions.[0].id", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].who", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].current", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].activity", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].exception", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].ipAddr", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].countryFlag", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].where", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].browser", notNullValue()))
                .andExpect(jsonPath("$.inactiveSessions.[0].what", notNullValue()));

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.superadminService).getSessions(requestAccessToken);
    }

    @Test
    void renewManuallyTest() throws Exception {
        // Arrange
        var sessionId = UserSessionId.random();

        // Act
        this.mvc.perform(post("/superadmin/sessions/" + sessionId + "/renew/manually"))
                .andExpect(status().isOk());

        // Assert
        verify(this.usersSessionsService).enableUserRequestMetadataRenewManually(sessionId);
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Arrange
        var sessionId = UserSessionId.random();

        // Act
        this.mvc.perform(delete("/superadmin/sessions/" + sessionId))
                .andExpect(status().isOk());

        // Assert
        verify(this.usersSessionsService).deleteById(sessionId);
    }

    @Test
    void deleteAllExceptCurrentTest() throws Exception {
        // Arrange
        var requestAccessToken = RequestAccessToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);

        // Act
        this.mvc.perform(delete("/superadmin/sessions"))
                .andExpect(status().isOk());

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.usersSessionsService).deleteAllExceptCurrentAsSuperuser(requestAccessToken);
    }
}
