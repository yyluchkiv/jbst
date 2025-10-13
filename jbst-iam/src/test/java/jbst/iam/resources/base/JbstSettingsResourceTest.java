package jbst.iam.resources.base;

import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.configurations.TestRunnerResources1;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.settings.AbstractJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.state").doesNotExist())
                .andExpect(jsonPath("$.stage").doesNotExist())
                .andExpect(jsonPath("$.stagesCount").doesNotExist())
                .andExpect(jsonPath("$.createdBy").exists())
                .andExpect(jsonPath("$.createdAt").exists());

        // Assert
        verify(this.settingsService).getSettings();
    }
}
