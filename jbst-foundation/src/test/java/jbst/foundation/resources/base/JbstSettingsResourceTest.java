package jbst.foundation.resources.base;

import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.JbstRequestJbstSettings;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.settings.JbstSettingsService;
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
    private final JbstSettingsService settingsService;

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
        when(this.settingsService.getSettings()).thenReturn(JbstSettings.fixed());

        // Act
        this.mvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.createdUTC").exists())
                .andExpect(jsonPath("$.updatedUTC").exists())
                .andExpect(jsonPath("$.hardwareMonitoringThresholds").exists());

        // Assert
        verify(this.settingsService).getSettings();
    }

    @Test
    void saveJbstSettings() throws Exception {
        // Arrange
        var request = JbstRequestJbstSettings.fixed();
        when(this.settingsService.getSettings()).thenReturn(JbstSettings.fixed());
        when(this.currentSessionAssistant.getCurrentJwtUser()).thenReturn(JbstJwtUser.fixed());

        // Act
        this.mvc.perform(
                        post("/settings")
                                .content(this.objectMapper.writeValueAsString(JbstRequestJbstSettings.fixed()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$.createdUTC").exists())
                .andExpect(jsonPath("$.updatedUTC").exists())
                .andExpect(jsonPath("$.hardwareMonitoringThresholds").exists());

        // Assert
        verify(this.currentSessionAssistant).getCurrentJwtUser();
        verify(this.settingsService).saveSettings(eq(Username.fixed()), eq(request));
        verify(this.settingsService).getSettings();
    }
}
