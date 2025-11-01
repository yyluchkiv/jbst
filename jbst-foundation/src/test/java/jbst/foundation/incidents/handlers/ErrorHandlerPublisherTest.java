package jbst.foundation.incidents.handlers;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
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
class ErrorHandlerPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        ErrorHandlerPublisher errorHandlerPublisher() {
            return new ErrorHandlerPublisher(
                    this.incidentsPublisher()
            );
        }
    }

    // Publisher
    private final JbstIncidentsPublisher incidentsPublisher;

    private final ErrorHandlerPublisher componentUnderTest;

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
    void handleErrorTest() {
        // Arrange
        var throwable = mock(Throwable.class);

        // Act
        this.componentUnderTest.handleError(throwable);

        // Assert
        verify(this.incidentsPublisher).publishThrowable(throwable);
    }
}
