package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.TokenUnauthorizedException;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.dto.responses.ResponseRefreshTokens;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.security.CurrentClientUser;
import jbst.foundation.services.AuthenticationService;
import jbst.foundation.validators.BaseAuthenticationRequestsValidator;
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
public class BaseSecurityAuthenticationResource {

    // Services
    private final AuthenticationService authenticationService;
    // Validators
    private final BaseAuthenticationRequestsValidator baseAuthenticationRequestsValidator;

    @PostMapping("/login/standard")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser loginStandard(
            @RequestBody @Valid RequestUserLogin request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstLoginException {
        var credentials = this.baseAuthenticationRequestsValidator.validateLoginStandard(request);
        return this.authenticationService.asStandard(credentials, httpRequest, httpResponse);
    }

    @PostMapping("/login/magic-link")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser loginMagicLink(
            @RequestBody @Valid RequestMagicLinkToken request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws JbstLoginException {
        request = request.createReworkedUkraineZoneId();
        var userToken = this.baseAuthenticationRequestsValidator.validateLoginMagicLink(request);
        return this.authenticationService.asMagicLink(userToken, request, httpRequest, httpResponse);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AccessTokenNotFoundException {
        this.authenticationService.logout(httpRequest, httpResponse);
    }

    @PostMapping("/refreshToken")
    @ResponseStatus(HttpStatus.OK)
    public ResponseRefreshTokens refreshToken(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws TokenUnauthorizedException {
        return this.authenticationService.refreshToken(httpRequest, httpResponse);
    }
}
