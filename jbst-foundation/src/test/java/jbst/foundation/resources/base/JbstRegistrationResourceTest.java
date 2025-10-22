package jbst.foundation.resources.base;

import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.foundation.domain.events.EventRegistration0;
import jbst.foundation.domain.events.EventRegistration1;
import jbst.foundation.domain.events.EventRegistrationMagicLink;
import jbst.foundation.events.publishers.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.incidents.domain.registration.IncidentRegistration0;
import jbst.foundation.incidents.domain.registration.IncidentRegistration1;
import jbst.foundation.incidents.domain.registration.IncidentRegistrationMagicLink;
import jbst.foundation.services.JbstRegistrationService;
import jbst.foundation.validators.JbstRegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstRegistrationResourceTest extends TestRunnerResources1 {

    // Extension
    private final JbstExtensionService extensionService;
    // Services
    private final JbstRegistrationService registrationService;
    // Publishers
    private final SecurityJwtEventsPublisher securityJwtEventsPublisher;
    private final SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher;
    // Validators
    private final JbstRegistrationValidator registrationValidator;

    // Resource
    private final JbstRegistrationResource componentUnderTest;

    @BeforeEach
    void beforeEach() {
        this.standaloneSetupByResourceUnderTest(this.componentUnderTest);
        reset(
                this.extensionService,
                this.registrationService,
                this.securityJwtEventsPublisher,
                this.securityJwtIncidentsPublisher,
                this.registrationValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.extensionService,
                this.registrationService,
                this.securityJwtEventsPublisher,
                this.securityJwtIncidentsPublisher,
                this.registrationValidator
        );
    }

    @Test
    void registerMagicLink() throws Exception {
        // Arrange
        var request = RequestUserRegistrationMagicLink.hardcoded();

        // Act
        this.mvc.perform(
                        post("/registration/register-magiclink")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        verify(this.registrationValidator).validateRegistrationRequestMagicLink(request);
        verify(this.registrationService).registerMagicLink(request);
        verify(this.securityJwtEventsPublisher).publishRegistrationMagicLink(new EventRegistrationMagicLink(request));
        verify(this.securityJwtIncidentsPublisher).publishRegistrationMagicLink(IncidentRegistrationMagicLink.of(request));
        verify(this.extensionService).registerMagicLink(request.email());
    }

    @Test
    void register0() throws Exception {
        // Arrange
        var request = RequestUserRegistration0.hardcoded();

        // Act
        this.mvc.perform(
                        post("/registration/register0")
                                .content(this.objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        request = request.createReworkedUkraineZoneId();
        verify(this.registrationValidator).validateRegistrationRequest0(request);
        verify(this.registrationService).register0(request);
        verify(this.securityJwtEventsPublisher).publishRegistration0(new EventRegistration0(request));
        verify(this.securityJwtIncidentsPublisher).publishRegistration0(new IncidentRegistration0(request.username()));
        verify(this.extensionService).register0(request.username());
    }

    @Test
    void register1() throws Exception {
        // Arrange
        var request = RequestUserRegistration1.hardcoded();

        // Act
        this.mvc.perform(
                post("/registration/register1")
                        .content(this.objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        // Assert
        request = request.createReworkedUkraineZoneId();
        verify(this.registrationValidator).validateRegistrationRequest1(request);
        verify(this.registrationService).register1(request);
        verify(this.securityJwtEventsPublisher).publishRegistration1(new EventRegistration1(request));
        verify(this.securityJwtIncidentsPublisher).publishRegistration1(new IncidentRegistration1(request.username()));
        verify(this.extensionService).register1(request.username());
    }
}
