package jbst.iam.resources.base;

import jbst.foundation.domain.base.Username;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.iam.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.iam.settings.AbstractJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstSettingsResourceTest extends TestRunnerResources1 {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final AbstractJbstSettingsService settingsService;

    // Resource
    private final JbstSettingsResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.currentSessionAssistant,
                this.settingsService
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.currentSessionAssistant,
                this.settingsService
        );
    }

    @Test
    void getJbstSettings() throws Exception {
        // Arrange
        when(this.settingsService.getSettings()).thenReturn(JbstSettings.hardcoded());

        // Act
        this.mvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$.createdBy").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedBy").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.hardwareMonitoringThresholds").exists());

        // Assert
        verify(this.settingsService).getSettings();
    }

    @Test
    void saveJbstSettings() throws Exception {
        // Arrange
        var request = RequestJbstSettings.hardcoded();
        when(this.settingsService.getSettings()).thenReturn(JbstSettings.hardcoded());
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(JwtUser.hardcoded());

        // Act
        this.mvc.perform(
                        post("/settings")
                                .content(this.objectMapper.writeValueAsString(RequestJbstSettings.hardcoded()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$.createdBy").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedBy").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.hardwareMonitoringThresholds").exists());

        // Assert
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.settingsService).saveSettings(eq(Username.hardcoded()), eq(request));
        verify(this.settingsService).getSettings();
    }
}
