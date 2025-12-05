package jbst.foundation.incidents.handlers;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.incidents.domain.JbstIncident;
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

import static jbst.foundation.domain.random.JbstRandom.*;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class JbstAsyncUncaughtExceptionHandlerPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstAsyncUncaughtExceptionHandlerPublisher asyncUncaughtExceptionHandlerPublisher() {
            return new JbstAsyncUncaughtExceptionHandlerPublisher(
                    this.incidentsPublisher()
            );
        }
    }

    // Publisher
    private final JbstIncidentsPublisher incidentsPublisher;

    private final JbstAsyncUncaughtExceptionHandlerPublisher componentUnderTest;

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
        verify(this.incidentsPublisher).publishIncident(new JbstIncident(throwable, method, Arrays.asList(params)));
    }
}
