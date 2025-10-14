package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jbst.foundation.domain.exceptions.authentication.JbstPasswordResetException;
import jbst.foundation.domain.exceptions.base.TooManyRequestsException;
import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.foundation.domain.exceptions.tokens.UserTokenValidationException;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.dto.requests.RequestUserEmail;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.RequestUserTokenMagicLink;
import jbst.foundation.services.BaseUsersService;
import jbst.foundation.services.BaseUsersTokensService;
import jbst.foundation.services.RateLimitsService;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.validators.BaseUsersTokensRequestsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

// Swagger
@Tag(name = "[jbst] Tokens API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityUsersTokensResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final RateLimitsService rateLimitsService;
    private final BaseUsersTokensService baseUsersTokensService;
    private final BaseUsersService baseUsersService;
    private final UsersEmailsService usersEmailsService;
    // Validators
    private final BaseUsersTokensRequestsValidator baseUsersTokensRequestsValidator;
    // Incidents
    private final IncidentPublisher incidentPublisher;
    // Properties
    private final JbstProperties jbstProperties;

    @PostMapping("/magic-link")
    @ResponseStatus(HttpStatus.OK)
    public void magicLink(@RequestBody @Valid RequestUserTokenMagicLink request) throws TooManyRequestsException {
        this.rateLimitsService.acquireMagicLinkOrThrow(request.email());
        var userToken = this.baseUsersTokensService.getOrCreate(request.asRequestUserToken());
        this.usersEmailsService.executeMagicLink(userToken);
    }

    @PostMapping("/email/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void executeConfirmEmail() throws TooManyRequestsException {
        var user = this.currentSessionAssistant.getCurrentJwtUser();
        this.baseUsersTokensRequestsValidator.validateExecuteConfirmEmail(user);
        this.rateLimitsService.acquireEmailConfirmationOrThrow(user);
        var userToken = this.baseUsersTokensService.getOrCreate(user.getRequestUserTokenAsEmailConfirmation());
        this.usersEmailsService.executeEmailConfirmation(userToken);
    }

    @ApiResponse(responseCode = "302", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping("/email/confirm")
    public RedirectView confirmEmail(
            RedirectAttributes redirectAttributes,
            @RequestParam("token") String token
    ) {
        var redirectView = new RedirectView(this.jbstProperties.getEmailConfirmationRedirectLink());
        try {
            this.baseUsersTokensRequestsValidator.validateEmailConfirmationToken(token);
            this.baseUsersTokensService.confirmEmail(token);
            redirectAttributes.addAttribute("code", 1);
            return redirectView;
        } catch (UserTokenValidationException | UserEmailConfirmException ex) {
            redirectAttributes.addAttribute("code", 0);
            return redirectView;
        } catch (RuntimeException ex) {
            this.incidentPublisher.publishThrowable(ex);
            redirectAttributes.addAttribute("code", 0);
            return redirectView;
        }
    }

    @PostMapping("/password/reset")
    @ResponseStatus(HttpStatus.OK)
    public void executeResetPassword(@RequestBody @Valid RequestUserEmail request) {
        try {
            var user = this.baseUsersService.findByEmail(request.email());
            this.baseUsersTokensRequestsValidator.validateExecuteResetPassword(user);
            var userToken = this.baseUsersTokensService.getOrCreate(user.getRequestUserTokenAsPasswordReset());
            this.usersEmailsService.executePasswordReset(userToken);
        } catch (JbstPasswordResetException ex) {
            // ignored
        }
    }

    @PatchMapping("/password/reset")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@RequestBody @Valid RequestUserPasswordReset request) throws UserTokenValidationException {
        this.baseUsersTokensRequestsValidator.validatePasswordReset(request);
        this.baseUsersService.resetPassword(request);
    }
}
