package jbst.iam.crons;

import jbst.foundation.crons.UsersTokensCron;
import jbst.foundation.domain.properties.base.Cron;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.repositories.UsersTokensRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersTokensCronTest {

    public static Stream<Arguments> cronArgs() {
        return Stream.of(
                Arguments.of(Cron.enabled()),
                Arguments.of(Cron.disabled())
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        UsersTokensRepository usersTokensRepository() {
            return mock(UsersTokensRepository.class);
        }

        @Bean
        IncidentPublisher incidentPublisher() {
            return mock(IncidentPublisher.class);
        }

        @Bean
        UsersTokensCron sessionsCron() {
            return new UsersTokensCron(
                    this.usersTokensRepository(),
                    this.incidentPublisher()
            );
        }
    }

    // Repository
    private final UsersTokensRepository usersTokensRepository;
    // Incidents
    private final IncidentPublisher incidentPublisher;

    private final UsersTokensCron componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersTokensRepository,
                this.incidentPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersTokensRepository,
                this.incidentPublisher
        );
    }

    @Test
    void processExceptionTest() {
        // Arrange
        var ex = new Exception();

        // Act
        this.componentUnderTest.processException(ex);

        // Assert
        verify(this.incidentPublisher).publishThrowable(ex);
    }

    @SuppressWarnings("unused")
    @ParameterizedTest
    @MethodSource("cronArgs")
    void cleanupTest(Cron cron) {
        // Act
        this.componentUnderTest.cleanup();

        // Assert
        verify(this.usersTokensRepository).cleanupExpired();
        verify(this.usersTokensRepository).cleanupUsed();
    }
}
