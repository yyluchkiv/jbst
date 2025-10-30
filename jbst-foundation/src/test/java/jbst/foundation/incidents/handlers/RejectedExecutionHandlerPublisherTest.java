package jbst.foundation.incidents.handlers;

import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class RejectedExecutionHandlerPublisherTest {

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        RejectedExecutionHandlerPublisher rejectedExecutionHandlerPublisher() {
            return new RejectedExecutionHandlerPublisher(
                    this.incidentsPublisher()
            );
        }
    }

    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    private final RejectedExecutionHandlerPublisher componentUnderTest;

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
    void rejectedExecutionTest() {
        // Arrange
        var runnableName = randomString();
        var executorName = randomString();
        var runnable = mock(Runnable.class);
        when(runnable.toString()).thenReturn(runnableName);
        var executor = mock(ThreadPoolExecutor.class);
        when(executor.toString()).thenReturn(executorName);

        // Act
        var throwable = catchThrowable(() -> this.componentUnderTest.rejectedExecution(runnable, executor));

        // Assert
        var message = "Task " + runnableName + " rejected from " + executorName;
        var exceptionAC = ArgumentCaptor.forClass(RejectedExecutionException.class);
        verify(this.incidentsPublisher).publishThrowable(exceptionAC.capture());
        assertThat(exceptionAC.getValue().getMessage()).isEqualTo(message);
        assertThat(throwable).isNotNull();
        assertThat(throwable.getClass()).isEqualTo(RejectedExecutionException.class);
        assertThat(throwable.getMessage()).isEqualTo(message);
    }
}
