package jbst.foundation.incidents.feigns.clients.impl;

import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
import jbst.foundation.incidents.feigns.definitions.IncidentClientDefinition;
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

import static jbst.foundation.utilities.random.RandomUtility.randomFeignException;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class IncidentClientImplTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        IncidentClientDefinition incidentClientDefinition() {
            return mock(IncidentClientDefinition.class);
        }

        @Bean
        IncidentClient incidentClient() {
            return new IncidentClient(
                    this.incidentClientDefinition()
            );
        }
    }

    // Definitions
    private final IncidentClientDefinition incidentClientDefinition;

    private final IncidentClient componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentClientDefinition
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentClientDefinition
        );
    }

    @Test
    void registerIncidentExceptionTest() {
        // Arrange
        var incident = mock(Incident.class);
        doThrow(randomFeignException()).when(incidentClientDefinition).registerIncident(incident);

        // Act
        this.componentUnderTest.registerIncident(incident);

        // Assert
        verify(this.incidentClientDefinition).registerIncident(incident);
        // LOGGER ignored
    }

    @Test
    void registerIncidentTest() {
        // Arrange
        var incident = mock(Incident.class);

        // Act
        this.componentUnderTest.registerIncident(incident);

        // Assert
        verify(this.incidentClientDefinition).registerIncident(incident);
    }
}
