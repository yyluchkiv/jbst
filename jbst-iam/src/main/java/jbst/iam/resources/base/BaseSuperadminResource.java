package jbst.iam.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.iam.assistants.current.CurrentSessionAssistant;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.iam.domain.identifiers.UserSessionId;
import jbst.iam.services.BaseSuperadminService;
import jbst.iam.services.BaseUsersSessionsService;
import jbst.iam.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;

import java.util.List;

// Swagger
@Tag(name = "[jbst] Superadmin API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSuperadminResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseSuperadminService baseSuperadminService;
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;

    // =================================================================================================================
    // Server
    // =================================================================================================================

    @GetMapping("/server/reset/status")
    public ResetServerStatus getResetServerStatus() {
        return this.baseSuperadminService.getResetServerStatus();
    }

    @PostMapping("/server/reset")
    public void resetServer() {
        var user = this.currentSessionAssistant.getCurrentJwtUser();
        this.baseSuperadminService.resetServerBy(user);
    }

    // =================================================================================================================
    // Invitations
    // =================================================================================================================

    @GetMapping("/invitations/unused")
    public List<ResponseInvitation> getUnusedInvitations() {
        return this.baseSuperadminService.findUnused();
    }

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================

    @GetMapping("/sessions")
    public ResponseSuperadminSessionsTable getSessions(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.baseSuperadminService.getSessions(cookie);
    }

    @PostMapping("/sessions/{sessionId}/renew/manually")
    public void renewManually(@PathVariable UserSessionId sessionId) {
        this.baseUsersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable UserSessionId sessionId) {
        this.baseUsersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping("/sessions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.baseUsersSessionsService.deleteAllExceptCurrentAsSuperuser(cookie);
    }
}

