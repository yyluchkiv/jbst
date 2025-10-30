package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.dto.responses.ResponseInvitation;
import jbst.foundation.domain.dto.responses.ResponseSuperadminSessionsTable;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import jbst.foundation.services.JbstSuperadminService;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Swagger
@Tag(name = "[jbst] Superadmin API")
// Spring
@Slf4j
@PreAuthorize("hasAuthority('" + AbstractAuthority.SUPERADMIN + "')")
@JbstResource
@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstSuperadminResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstSuperadminService superadminService;
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;

    // =================================================================================================================
    // Server
    // =================================================================================================================

    @GetMapping("/server/reset/status")
    public ResetServerStatus getResetServerStatus() {
        return this.superadminService.getResetServerStatus();
    }

    @PostMapping("/server/reset")
    public void resetServer() {
        var user = this.currentSessionAssistant.getCurrentJwtUser();
        this.superadminService.resetServerBy(user);
    }

    // =================================================================================================================
    // Invitations
    // =================================================================================================================

    @GetMapping("/invitations/unused")
    public List<ResponseInvitation> getUnusedInvitations() {
        return this.superadminService.findUnused();
    }

    // =================================================================================================================
    // Users
    // =================================================================================================================

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================

    @GetMapping("/sessions")
    public ResponseSuperadminSessionsTable getSessions(HttpServletRequest httpRequest) throws JbstAccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.superadminService.getSessions(cookie);
    }

    @PostMapping("/sessions/{sessionId}/renew/manually")
    public void renewManually(@PathVariable UserSessionId sessionId) {
        this.usersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable UserSessionId sessionId) {
        this.usersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping("/sessions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws JbstAccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.usersSessionsService.deleteAllExceptCurrentAsSuperuser(cookie);
    }
}

