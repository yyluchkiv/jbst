package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.dto.responses.JbstResponseSuperadminSessionsTable;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.system.JbstSystemResetServerStatus;
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
    public JbstSystemResetServerStatus getResetServerStatus() {
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
    public List<JbstResponseInvitation> findInvitationsUnused() {
        return this.superadminService.findInvitationsUnused();
    }

    // =================================================================================================================
    // Users
    // =================================================================================================================
    @GetMapping("/users")
    public JbstUsers findUsersExcept() {
        var currentUsername = this.currentSessionAssistant.getCurrentUsername();
        return this.superadminService.findUsersExcept(currentUsername);
    }

    @PostMapping("/users/{username}/disable")
    public void disableUser(@PathVariable Username username) {
        this.superadminService.disableUser(username);
    }

    // =================================================================================================================
    // Users Sessions
    // =================================================================================================================
    @GetMapping("/sessions")
    public JbstResponseSuperadminSessionsTable getSessions(HttpServletRequest httpRequest) throws JbstExceptions.AccessTokenNotFound {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.superadminService.getSessions(cookie);
    }

    @PostMapping("/sessions/{sessionId}/renew/manually")
    public void renewManually(@PathVariable JbstUserSessionId sessionId) {
        this.usersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable JbstUserSessionId sessionId) {
        this.usersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping("/sessions")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws JbstExceptions.AccessTokenNotFound {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.usersSessionsService.deleteAllExceptCurrentAsSuperuser(cookie);
    }
}

