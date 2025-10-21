package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.exceptions.tokens.JbstAccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.JbstTokenUnauthorizedException;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.services.base.JbstAuthenticationService;
import jbst.foundation.validators.base.JbstAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Authentication API")
// Spring
@JbstResource
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstAuthenticationResource {

    // Assistants
    private final CurrentSessionAssistant currentSessionAssistant;
    // Services
    private final JbstAuthenticationService authenticationService;
    private final JbstExtensionService extensionService;
    // Validators
    private final JbstAuthenticationRequestsValidator authenticationRequestsValidator;

    @PostMapping("/login/standard")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser authenticateAsStandard(
            @RequestBody @Valid RequestUserLogin request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstLoginException {
        var credentials = this.authenticationRequestsValidator.validateLoginStandard(request);
        var username = this.authenticationService.asStandard(credentials, httpRequest, httpResponse);
        this.extensionService.authenticateAsStandard(username, httpRequest, httpResponse);
        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @PostMapping("/login/magiclink")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser authenticateAsMagicLink(
            @RequestBody @Valid RequestMagicLinkToken request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstLoginException {
        request = request.createReworkedUkraineZoneId();
        var credentials = this.authenticationRequestsValidator.validateLoginMagicLink(request);
        var username = this.authenticationService.asMagicLink(credentials, httpRequest, httpResponse);
        this.extensionService.authenticateAsMagicLink(username, httpRequest, httpResponse);
        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstAccessTokenNotFoundException {
        this.authenticationService.logout(httpRequest, httpResponse);
    }

    @PostMapping("/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstTokenUnauthorizedException {
        return this.authenticationService.refreshToken(httpRequest, httpResponse);
    }
}
