package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.databases.JbstSettings;
import jbst.foundation.domain.dto.requests.RequestJbstSettings;
import jbst.foundation.settings.JbstSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Settings API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSettingsResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstSettingsService settingsService;

    // =================================================================================================================
    // Server
    // =================================================================================================================

    @GetMapping
    public JbstSettings getJbstSettings() {
        return this.settingsService.getSettings();
    }

    @PostMapping
    public JbstSettings saveJbstSettings(@RequestBody @Valid RequestJbstSettings request) {
        this.settingsService.saveSettings(
                this.currentSessionAssistant.getCurrentJwtUser().username(),
                request
        );
        return this.settingsService.getSettings();
    }
}

