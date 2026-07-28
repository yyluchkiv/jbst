package jbst.foundation.resources.base;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserSession;
import jbst.foundation.domain.dto.requests.JbstRequestAccessToken;
import jbst.foundation.domain.dto.responses.JbstResponseUserSession2;
import jbst.foundation.domain.dto.responses.JbstResponseUserSessionsTable;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.security.JbstCurrentClientUser;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import jbst.foundation.validators.JbstUsersSessionsValidator;
import lombok.RequiredArgsConstructor;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;

import static jbst.foundation.domain.random.JbstRandomEntities.list345;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstUsersSessionsResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;
    // Validators
    private final JbstUsersSessionsValidator usersSessionsValidator;

    // Resource
    private final JbstUsersSessionsResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.usersSessionsService,
                this.tokensProvider,
                this.usersSessionsValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.usersSessionsService,
                this.tokensProvider,
                this.usersSessionsValidator
        );
    }

    @Test
    void getSessionsTableTest() throws Exception {
        // Arrange
        var userSessionsTables = JbstResponseUserSessionsTable.of(list345(JbstResponseUserSession2.class));
        var requestAccessToken = JbstRequestAccessToken.random();
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(requestAccessToken);
        when(this.currentSessionAssistant.getCurrentUserDbSessionsTable(requestAccessToken)).thenReturn(userSessionsTables);

        // Act
        this.mvc.perform(get("/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessions", hasSize(userSessionsTables.sessions().size())))
                .andExpect(jsonPath("$.anyPresent", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.anyProblem", instanceOf(Boolean.class)));

        // Assert
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.currentSessionAssistant).getCurrentUserDbSessionsTable(requestAccessToken);
    }

    @Test
    void getCurrentClientUserCronEnabledTest() throws Exception {
        // Arrange
        var currentClientUser = JbstCurrentClientUser.random();
        var session = JbstUserSession.randomPersistedSession();
        when(this.currentSessionAssistant.getCurrentClientUser()).thenReturn(currentClientUser);
        when(this.currentSessionAssistant.getCurrentUserSession(any(HttpServletRequest.class))).thenReturn(session);

        // Act
        this.mvc.perform(get("/sessions/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(currentClientUser.getUsername().value()))
                .andExpect(jsonPath("$.email").value(currentClientUser.getEmail().value()))
                .andExpect(jsonPath("$.name").value(currentClientUser.getName()))
                .andExpect(jsonPath("$.zoneId").value(currentClientUser.getZoneId().getId()))
                .andExpect(jsonPath("$.authorities").isEmpty())
                .andExpect(jsonPath("$.attributes").isEmpty())
                .andExpect(jsonPath("$.zoneId", new BaseMatcher<String>() {

                    @Override
                    public void describeTo(Description description) {
                        // no actions
                    }

                    @Override
                    public boolean matches(Object o) {
                        var zoneId = ZoneId.of(o.toString());
                        return ZoneId.getAvailableZoneIds().contains(zoneId.getId());
                    }
                }));

        // Assert
        verify(this.currentSessionAssistant).getCurrentClientUser();
        verify(this.currentSessionAssistant).getCurrentUserSession(any(HttpServletRequest.class));
        verify(this.usersSessionsService).renewUserRequestMetadata(eq(session), any(HttpServletRequest.class));
    }

    @Test
    void renewManuallyTest() throws Exception {
        // Arrange
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(Username.fixed());

        // Act
        this.mvc.perform(
                        post("/sessions/" + JbstUserSessionId.fixed() + "/renew/manually")
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.usersSessionsService).assertAccess(Username.fixed(), JbstUserSessionId.fixed());
        verify(this.usersSessionsService).enableUserRequestMetadataRenewManually(JbstUserSessionId.fixed());
    }

    @Test
    void deleteByIdTest() throws Exception {
        // Arrange
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(Username.fixed());

        // Act
        this.mvc.perform(delete("/sessions/" + JbstUserSessionId.fixed()))
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.usersSessionsService).assertAccess(Username.fixed(), JbstUserSessionId.fixed());
        verify(this.usersSessionsService).deleteById(JbstUserSessionId.fixed());
    }

    @Test
    void deleteAllExceptCurrent() throws Exception {
        // Arrange
        when(this.currentSessionAssistant.getCurrentUsername()).thenReturn(Username.fixed());
        when(this.tokensProvider.readRequestAccessToken(any(HttpServletRequest.class))).thenReturn(JbstRequestAccessToken.fixed());

        // Act
        this.mvc.perform(delete("/sessions"))
                .andExpect(status().isOk());

        // Assert
        verify(this.currentSessionAssistant).getCurrentUsername();
        verify(this.tokensProvider).readRequestAccessToken(any(HttpServletRequest.class));
        verify(this.usersSessionsService).deleteAllExceptCurrent(Username.fixed(), JbstRequestAccessToken.fixed());
    }
}
