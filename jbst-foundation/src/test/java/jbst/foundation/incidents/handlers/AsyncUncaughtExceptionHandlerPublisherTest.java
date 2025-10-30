package jbst.foundation.incidents.handlers;

import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.Incident;
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

import java.util.Arrays;

import static jbst.foundation.utilities.random.RandomUtility.*;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AsyncUncaughtExceptionHandlerPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        AsyncUncaughtExceptionHandlerPublisher asyncUncaughtExceptionHandlerPublisher() {
            return new AsyncUncaughtExceptionHandlerPublisher(
                    this.incidentsPublisher()
            );
        }
    }

    // Publisher
    private final JbstIncidentsPublisher incidentsPublisher;

    private final AsyncUncaughtExceptionHandlerPublisher componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.incidentsPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.incidentsPublisher
        );
    }

    @Test
    void handleUncaughtExceptionTest() {
        // Arrange
        var throwable = mock(Throwable.class);
        var method = randomMethod();
        var params = new Object[] { randomString(), randomLong() };

        // Act
        this.componentUnderTest.handleUncaughtException(throwable, method, params);

        // Assert
        verify(this.incidentsPublisher).publishIncident(new Incident(throwable, method, Arrays.asList(params)));
    }
}
