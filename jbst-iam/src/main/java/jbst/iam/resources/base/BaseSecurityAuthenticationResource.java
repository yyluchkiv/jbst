package jbst.iam.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jbst.foundation.domain.exceptions.tokens.AccessTokenNotFoundException;
import jbst.foundation.domain.exceptions.tokens.TokenUnauthorizedException;
import jbst.iam.annotations.AbstractJbstBaseSecurityResource;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.dto.responses.ResponseRefreshTokens;
import jbst.iam.domain.exceptions.LoginException;
import jbst.iam.domain.security.CurrentClientUser;
import jbst.iam.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Authentication API")
// Spring
@AbstractJbstBaseSecurityResource
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseSecurityAuthenticationResource {

    // Services
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser login(@RequestBody @Valid RequestUserLogin request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws LoginException {
        return this.authenticationService.asStandard(request, httpRequest, httpResponse);
    }

    // TODO [YYL, MagicLink]
    @PostMapping("/authenticate/magic-link")
    @ResponseStatus(HttpStatus.OK)
    public CurrentClientUser authenticateWithMagicLink(
            @RequestBody @Valid RequestMagicLinkToken request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws TokenUnauthorizedException {
        return this.authenticationService.asMagicLink(request, httpRequest, httpResponse);
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
