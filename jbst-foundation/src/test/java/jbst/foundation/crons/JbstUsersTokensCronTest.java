package jbst.foundation.crons;

import jbst.foundation.domain.properties.base.JbstPropertyCron;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.JbstUsersTokensRepository;
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
public class JbstUsersTokensCronTest {

    public static Stream<Arguments> cronArgs() {
        return Stream.of(
                Arguments.of(JbstPropertyCron.enabled()),
                Arguments.of(JbstPropertyCron.disabled())
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        JbstUsersTokensRepository usersTokensRepository() {
            return mock(JbstUsersTokensRepository.class);
        }

        @Bean
        JbstIncidentsPublisher incidentsPublisher() {
            return mock(JbstIncidentsPublisher.class);
        }

        @Bean
        JbstUsersTokensCron sessionsCron() {
            return new JbstUsersTokensCron(
                    this.usersTokensRepository(),
                    this.incidentsPublisher()
            );
        }
    }

    // Repository
    private final JbstUsersTokensRepository usersTokensRepository;
    // Incidents
    private final JbstIncidentsPublisher incidentsPublisher;

    private final JbstUsersTokensCron componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.usersTokensRepository,
                this.incidentsPublisher
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.usersTokensRepository,
                this.incidentsPublisher
        );
    }

    @Test
    void processExceptionTest() {
        // Arrange
        var ex = new Exception();

        // Act
        this.componentUnderTest.processException(ex);

        // Assert
        verify(this.incidentsPublisher).publishThrowable(ex);
    }

    @SuppressWarnings("unused")
    @ParameterizedTest
    @MethodSource("cronArgs")
    void cleanupTest(JbstPropertyCron cron) {
        // Act
        this.componentUnderTest.cleanup();

        // Assert
        verify(this.usersTokensRepository).cleanupExpired();
        verify(this.usersTokensRepository).cleanupUsed();
    }
}
