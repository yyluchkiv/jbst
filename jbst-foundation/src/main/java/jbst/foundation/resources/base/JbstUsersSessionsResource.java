package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserSessionId;
import jbst.foundation.domain.security.JbstCurrentClientUser;
import jbst.foundation.services.JbstUsersSessionsService;
import jbst.foundation.tokens.facade.JbstTokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Sessions API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstUsersSessionsResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstUsersSessionsService usersSessionsService;
    // Tokens
    private final JbstTokensProvider tokensProvider;

    @GetMapping
    public ResponseUserSessionsTable getSessionsTable(HttpServletRequest httpRequest) throws JbstExceptions.AccessTokenNotFound {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.currentSessionAssistant.getCurrentUserDbSessionsTable(cookie);
    }

    @GetMapping("/current")
    public JbstCurrentClientUser getCurrentClientUser(HttpServletRequest httpRequest) throws JbstExceptions.AccessTokenNotFound {
        var user = this.currentSessionAssistant.getCurrentClientUser();
        var session = this.currentSessionAssistant.getCurrentUserSession(httpRequest);
        this.usersSessionsService.renewUserRequestMetadata(session, httpRequest);
        return user;
    }

    @PostMapping("/{sessionId}/renew/manually")
    public void renewManually(@PathVariable JbstUserSessionId sessionId) {
        var username = this.currentSessionAssistant.getCurrentUsername();
        this.usersSessionsService.assertAccess(username, sessionId);
        this.usersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable JbstUserSessionId sessionId) {
        var username = this.currentSessionAssistant.getCurrentUsername();
        this.usersSessionsService.assertAccess(username, sessionId);
        this.usersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws JbstExceptions.AccessTokenNotFound {
        var username = this.currentSessionAssistant.getCurrentUsername();
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.usersSessionsService.deleteAllExceptCurrent(username, cookie);
    }
}

