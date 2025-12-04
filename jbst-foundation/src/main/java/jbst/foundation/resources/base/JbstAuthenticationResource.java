package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jbst.foundation.assistants.current.CurrentSessionAssistant;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.requests.JbstRequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.JbstRequestUserLogin;
import jbst.foundation.domain.dto.responses.JbstResponseRefreshTokens;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.security.JbstCurrentClientUser;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.services.base.JbstAuthenticationService;
import jbst.foundation.validators.base.JbstAuthenticationValidator;
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
    // Extension
    private final JbstExtensionService extensionService;
    // Services
    private final JbstAuthenticationService authenticationService;
    // Validators
    private final JbstAuthenticationValidator authenticationRequestsValidator;

    @PostMapping("/login/standard")
    @ResponseStatus(HttpStatus.OK)
    public JbstCurrentClientUser authenticateAsStandard(
            @RequestBody @Valid JbstRequestUserLogin request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstExceptions.Login {
        var credentials = this.authenticationRequestsValidator.validateLoginStandard(request);
        var username = this.authenticationService.asStandard(credentials, httpRequest, httpResponse);
        this.extensionService.authenticateAsStandard(username, httpRequest, httpResponse);
        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @PostMapping("/login/magiclink")
    @ResponseStatus(HttpStatus.OK)
    public JbstCurrentClientUser authenticateAsMagicLink(
            @RequestBody @Valid JbstRequestMagicLinkToken request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstExceptions.Login {
        request = request.createReworkedUkraineZoneId();
        var credentials = this.authenticationRequestsValidator.validateLoginMagicLink(request);
        var username = this.authenticationService.asMagicLink(credentials, httpRequest, httpResponse);
        this.extensionService.authenticateAsMagicLink(username, httpRequest, httpResponse);
        return this.currentSessionAssistant.getCurrentClientUser();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.AccessTokenNotFound {
        this.authenticationService.logout(httpRequest, httpResponse);
    }

    @PostMapping("/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public JbstResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws JbstExceptions.Unauthorized {
        return this.authenticationService.refreshToken(httpRequest, httpResponse);
    }
}
