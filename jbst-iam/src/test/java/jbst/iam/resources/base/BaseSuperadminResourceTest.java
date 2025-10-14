package jbst.iam.resources.base;

import jakarta.servlet.http.HttpServletRequest;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.iam.domain.dto.responses.ResponseUserSession2;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.jwt.RequestAccessToken;
import jbst.iam.services.BaseSuperadminService;
import jbst.iam.services.BaseUsersSessionsService;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.iam.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static jbst.foundation.utilities.random.EntityUtility.entity;
import static jbst.foundation.utilities.random.EntityUtility.list345;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BaseSuperadminResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseSuperadminService baseSuperadminService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;

    // Resource
    private final BaseSuperadminResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.baseSuperadminService,
                this.baseUsersSessionsService,
                this.tokensProvider
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.baseSuperadminService,
                this.baseUsersSessionsService,
                this.tokensProvider
        );
    }

    @Test
    void getResetServerStatusTest() throws Exception {
        // Arrange
        when(this.baseSuperadminService.getResetServerStatus()).thenReturn(ResetServerStatus.random());

        // Act
        this.mvc.perform(get("/superadmin/server/reset/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").doesNotExist())
                .andExpect(jsonPath("$.stage").doesNotExist())
                .andExpect(jsonPath("$.stagesCount").doesNotExist())
                .andExpect(jsonPath("$.percentage").exists())
                .andExpect(jsonPath("$.description").exists());

        // Assert
        verify(this.baseSuperadminService).getResetServerStatus();
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
        verify(this.baseSuperadminService).resetServerBy(user);
    }

    @Test
    void getUnusedInvitationsTest() throws Exception {
        // Arrange
        var codes = list345(ResponseInvitation.class);
        when(this.baseSuperadminService.findUnused()).thenReturn(codes);

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
        verify(this.baseSuperadminService).findUnused();
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
        when(this.baseSuperadminService.getSessions(requestAccessToken)).thenReturn(sessionsTable);

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
        verify(this.baseSuperadminService).getSessions(requestAccessToken);
    }

    @Test
    void renewManuallyTest() throws Exception {
        // Arrange
        var sessionId = UserSessionId.random();

        // Act
        this.mvc.perform(post("/superadmin/sessions/" + sessionId + "/renew/manually"))
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUsersSessionsService).enableUserRequestMetadataRenewManually(sessionId);
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Arrange
        var sessionId = UserSessionId.random();

        // Act
        this.mvc.perform(delete("/superadmin/sessions/" + sessionId))
                .andExpect(status().isOk());

        // Assert
        verify(this.baseUsersSessionsService).deleteById(sessionId);
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
        verify(this.baseUsersSessionsService).deleteAllExceptCurrentAsSuperuser(requestAccessToken);
    }
}
