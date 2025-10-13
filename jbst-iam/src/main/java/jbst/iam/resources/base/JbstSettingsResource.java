package jbst.iam.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jbst.iam.annotations.AbstractJbstResource;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.domain.db.JbstSettings;
import jbst.iam.domain.dto.requests.RequestJbstSettings;
import jbst.iam.settings.AbstractJbstSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Settings API")
// Spring
@Slf4j
@AbstractJbstResource
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSettingsResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final AbstractJbstSettingsService settingsService;

    // =================================================================================================================
    // Server
    // =================================================================================================================

    @GetMapping
    public JbstSettings getJbstSettings() {
        return this.settingsService.getSettings();
    }

    @PostMapping
    public JbstSettings saveJbstSettings(@RequestBody @Valid RequestJbstSettings request) {
        return this.settingsService.saveSettings(
                this.currentSessionAssistant.getCurrentJwtUser().username(),
                request
        );
    }
}

