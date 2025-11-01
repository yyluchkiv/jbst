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
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.incidents.domain.registration.IncidentRegistration0;
import jbst.foundation.incidents.domain.registration.IncidentRegistration1;
import jbst.foundation.incidents.domain.registration.IncidentRegistrationMagicLink;
import jbst.foundation.services.JbstRegistrationService;
import jbst.foundation.services.base.JbstRateLimitsService;
import jbst.foundation.validators.JbstRegistrationValidator;
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

    // Extension
    private final JbstExtensionService extensionService;
    // Services
    private final JbstRateLimitsService rateLimitsService;
    private final JbstRegistrationService registrationService;
    // Publishers
    private final JbstEventsPublisher eventsPublisher;
    private final JbstIncidentsPublisher incidentsPublisher;
    // Validators
    private final JbstRegistrationValidator registrationValidator;

    @PostMapping("/register-magiclink")
    @ResponseStatus(HttpStatus.OK)
    public void registerMagicLink(@RequestBody @Valid RequestUserRegistrationMagicLink request) throws JbstTooManyRequestsException {
        this.rateLimitsService.acquireMagicLinkOrThrow(request.email());
        this.registrationValidator.validateRegistrationRequestMagicLink(request);
        this.registrationService.registerMagicLink(request);
        this.eventsPublisher.publishRegistrationMagicLink(new EventRegistrationMagicLink(request));
        this.incidentsPublisher.publishRegistrationMagicLink(IncidentRegistrationMagicLink.of(request));
        this.extensionService.registerMagicLink(request.email());
    }

    @PostMapping("/register0")
    @ResponseStatus(HttpStatus.OK)
    public void register0(@RequestBody @Valid RequestUserRegistration0 request) throws JbstRegistrationException {
        request = request.createReworkedUkraineZoneId();
        this.registrationValidator.validateRegistrationRequest0(request);
        this.registrationService.register0(request);
        this.eventsPublisher.publishRegistration0(new EventRegistration0(request));
        this.incidentsPublisher.publishRegistration0(new IncidentRegistration0(request.username()));
        this.extensionService.register0(request.username());
    }

    @PostMapping("/register1")
    @ResponseStatus(HttpStatus.OK)
    public void register1(@RequestBody @Valid RequestUserRegistration1 request) throws JbstRegistrationException {
        request = request.createReworkedUkraineZoneId();
        this.registrationValidator.validateRegistrationRequest1(request);
        this.registrationService.register1(request);
        this.eventsPublisher.publishRegistration1(new EventRegistration1(request));
        this.incidentsPublisher.publishRegistration1(new IncidentRegistration1(request.username()));
        this.extensionService.register1(request.username());
    }
}
