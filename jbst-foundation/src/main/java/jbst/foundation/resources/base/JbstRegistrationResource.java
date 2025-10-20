package jbst.foundation.resources.base;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jbst.foundation.domain.annotations.JbstResource;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.foundation.domain.events.EventRegistration0;
import jbst.foundation.domain.events.EventRegistration1;
import jbst.foundation.domain.events.EventRegistrationMagicLink;
import jbst.foundation.domain.exceptions.authentication.JbstRegistrationException;
import jbst.foundation.domain.exceptions.base.JbstTooManyRequestsException;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.incidents.domain.registration.IncidentRegistration0;
import jbst.foundation.incidents.domain.registration.IncidentRegistration1;
import jbst.foundation.incidents.domain.registration.IncidentRegistrationMagicLink;
import jbst.foundation.services.BaseRegistrationService;
import jbst.foundation.services.RateLimitsService;
import jbst.foundation.validators.BaseRegistrationRequestsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// Swagger
@Tag(name = "[jbst] Registration API")
// Spring
@Slf4j
@JbstResource
@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstRegistrationResource {

    // Services
    private final RateLimitsService rateLimitsService;
    private final BaseRegistrationService baseRegistrationService;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtEventsPublisher;
    private final SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher;
    // Validators
    private final BaseRegistrationRequestsValidator baseRegistrationRequestsValidator;

    @PostMapping("/register-magiclink")
    @ResponseStatus(HttpStatus.OK)
    public void registerMagicLink(@RequestBody @Valid RequestUserRegistrationMagicLink request) throws JbstTooManyRequestsException {
        this.rateLimitsService.acquireMagicLinkOrThrow(request.email());
        this.baseRegistrationRequestsValidator.validateRegistrationRequestMagicLink(request);
        this.baseRegistrationService.registerMagicLink(request);
        this.securityJwtEventsPublisher.publishRegistrationMagicLink(new EventRegistrationMagicLink(request));
        this.securityJwtIncidentsPublisher.publishRegistrationMagicLink(IncidentRegistrationMagicLink.of(request));
    }

    @PostMapping("/register0")
    @ResponseStatus(HttpStatus.OK)
    public void register0(@RequestBody @Valid RequestUserRegistration0 request) throws JbstRegistrationException {
        request = request.createReworkedUkraineZoneId();
        this.baseRegistrationRequestsValidator.validateRegistrationRequest0(request);
        this.baseRegistrationService.register0(request);
        this.securityJwtEventsPublisher.publishRegistration0(new EventRegistration0(request));
        this.securityJwtIncidentsPublisher.publishRegistration0(new IncidentRegistration0(request.username()));
    }

    @PostMapping("/register1")
    @ResponseStatus(HttpStatus.OK)
    public void register1(@RequestBody @Valid RequestUserRegistration1 request) throws JbstRegistrationException {
        request = request.createReworkedUkraineZoneId();
        this.baseRegistrationRequestsValidator.validateRegistrationRequest1(request);
        this.baseRegistrationService.register1(request);
        this.securityJwtEventsPublisher.publishRegistration1(new EventRegistration1(request));
        this.securityJwtIncidentsPublisher.publishRegistration1(new IncidentRegistration1(request.username()));
    }
}
