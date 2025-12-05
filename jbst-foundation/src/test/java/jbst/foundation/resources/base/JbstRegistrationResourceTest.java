package jbst.foundation.resources.base;

import jbst.foundation.configurations.TestRunnerResources1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.domain.events.JbstEventRegistration0;
import jbst.foundation.domain.events.JbstEventRegistration1;
import jbst.foundation.domain.events.JbstEventRegistrationMagicLink;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.extension.JbstExtensionService;
import jbst.foundation.incidents.domain.registration.JbstIncidentRegistration0;
import jbst.foundation.incidents.domain.registration.JbstIncidentRegistration1;
import jbst.foundation.incidents.domain.registration.JbstIncidentRegistrationMagicLink;
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
    private final JbstEventsPublisher eventsPublisher;
    private final JbstIncidentsPublisher incidentsPublisher;
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
                this.eventsPublisher,
                this.incidentsPublisher,
                this.registrationValidator
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.extensionService,
                this.registrationService,
                this.eventsPublisher,
                this.incidentsPublisher,
                this.registrationValidator
        );
    }

    @Test
    void registerMagicLink() throws Exception {
        // Arrange
        var request = JbstRequestUserRegistrationMagicLink.hardcoded();

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
        verify(this.eventsPublisher).publishRegistrationMagicLink(new JbstEventRegistrationMagicLink(request));
        verify(this.incidentsPublisher).publishRegistrationMagicLink(JbstIncidentRegistrationMagicLink.of(request));
        verify(this.extensionService).registerMagicLink(request.email());
    }

    @Test
    void register0() throws Exception {
        // Arrange
        var request = JbstRequestUserRegistration0.hardcoded();

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
        verify(this.eventsPublisher).publishRegistration0(new JbstEventRegistration0(request));
        verify(this.incidentsPublisher).publishRegistration0(new JbstIncidentRegistration0(request.username()));
        verify(this.extensionService).register0(request.username());
    }

    @Test
    void register1() throws Exception {
        // Arrange
        var request = JbstRequestUserRegistration1.hardcoded();

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
        verify(this.eventsPublisher).publishRegistration1(new JbstEventRegistration1(request));
        verify(this.incidentsPublisher).publishRegistration1(new JbstIncidentRegistration1(request.username()));
        verify(this.extensionService).register1(request.username());
    }
}
