package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.dto.responses.ResponseUserSessionsTable;
import jbst.foundation.domain.ids.UserSessionId;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.tokens.facade.TokensProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;

// Swagger
@Tag(name = "[jbst] Sessions API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityUsersSessionsResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final BaseUsersSessionsService baseUsersSessionsService;
    // Tokens
    private final TokensProvider tokensProvider;

    @GetMapping
    public ResponseUserSessionsTable getSessionsTable(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        return this.currentSessionAssistant.getCurrentUserDbSessionsTable(cookie);
    }

    @GetMapping("/current")
    public CurrentClientUser getCurrentClientUser(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var user = this.currentSessionAssistant.getCurrentClientUser();
        var session = this.currentSessionAssistant.getCurrentUserSession(httpRequest);
        this.baseUsersSessionsService.renewUserRequestMetadata(session, httpRequest);
        return user;
    }

    @PostMapping("/{sessionId}/renew/manually")
    public void renewManually(@PathVariable UserSessionId sessionId) {
        var username = this.currentSessionAssistant.getCurrentUsername();
        this.baseUsersSessionsService.assertAccess(username, sessionId);
        this.baseUsersSessionsService.enableUserRequestMetadataRenewManually(sessionId);
    }

    @DeleteMapping("/{sessionId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable UserSessionId sessionId) {
        var username = this.currentSessionAssistant.getCurrentUsername();
        this.baseUsersSessionsService.assertAccess(username, sessionId);
        this.baseUsersSessionsService.deleteById(sessionId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllExceptCurrent(HttpServletRequest httpRequest) throws AccessTokenNotFoundException {
        var username = this.currentSessionAssistant.getCurrentUsername();
        var cookie = this.tokensProvider.readRequestAccessToken(httpRequest);
        this.baseUsersSessionsService.deleteAllExceptCurrent(username, cookie);
    }
}

