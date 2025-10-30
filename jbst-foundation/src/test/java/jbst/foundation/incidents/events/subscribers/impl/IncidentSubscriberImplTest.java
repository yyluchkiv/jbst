package jbst.foundation.incidents.events.subscribers.impl;

import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.events.subscribers.IncidentSubscriber;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class IncidentSubscriberImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        IncidentClient incidentClient() {
            return mock(IncidentClient.class);
        }

        @Bean
        IncidentSubscriber incidentPublisher() {
            return new IncidentSubscriberImpl(
                    this.incidentClient()
            );
        }
    }

    // Clients
    private final IncidentClient incidentClient;

    private final IncidentSubscriber componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentClient
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentClient
        );
    }

    @Test
    void onEventIncidentTest() {
        // Arrange
        var incident = Incident.random();

        // Act
        this.componentUnderTest.onEvent(incident);

        // Assert
        verify(this.incidentClient).registerIncident(incident);
    }
}
