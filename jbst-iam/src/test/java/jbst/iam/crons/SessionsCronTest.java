package jbst.iam.crons;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.Cron;
import jbst.foundation.domain.properties.configs.SecurityJwtConfigs;
import jbst.foundation.domain.properties.configs.security.jwt.SessionConfigs;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.services.BaseUsersSessionsService;
import jbst.foundation.sessions.SessionRegistry;
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

import static jbst.foundation.utilities.random.EntityUtility.set345;
import static org.mockito.Mockito.*;

@ExtendWith({ SpringExtension.class })
@ContextConfiguration(loader= AnnotationConfigContextLoader.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class SessionsCronTest {

    public static Stream<Arguments> cronArgs() {
        return Stream.of(
                Arguments.of(Cron.enabled()),
                Arguments.of(Cron.disabled())
        );
    }

    @Configuration
    static class ContextConfiguration {
        @Bean
        SessionRegistry sessionRegistry() {
            return mock(SessionRegistry.class);
        }

        @Bean
        BaseUsersSessionsService baseUsersSessionsService() {
            return mock(BaseUsersSessionsService.class);
        }

        @Bean
        IncidentPublisher incidentPublisher() {
            return mock(IncidentPublisher.class);
        }

        @Bean
        JbstProperties jbstProperties() {
            return mock(JbstProperties.class);
        }

        @Bean
        SessionsCron sessionsCron() {
            return new SessionsCron(
                    this.sessionRegistry(),
                    this.baseUsersSessionsService(),
                    this.incidentPublisher(),
                    this.jbstProperties()
            );
        }
    }

    private final SessionRegistry sessionRegistry;
    private final BaseUsersSessionsService baseUsersSessionsService;
    private final IncidentPublisher incidentPublisher;
    private final JbstProperties jbstProperties;

    private final SessionsCron componentUnderTest;

    @BeforeEach
    void beforeEach() {
        reset(
                this.sessionRegistry,
                this.baseUsersSessionsService,
                this.incidentPublisher,
                this.jbstProperties
        );
    }

    @AfterEach
    void afterEach() {
        verifyNoMoreInteractions(
                this.sessionRegistry,
                this.baseUsersSessionsService,
                this.incidentPublisher,
                this.jbstProperties
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

    @ParameterizedTest
    @MethodSource("cronArgs")
    void cleanByExpiredRefreshTokensTest(Cron cron) {
        // Arrange
        var usernames = set345(Username.class);
        if (cron.isEnabled()) {
            when(this.sessionRegistry.getActiveSessionsUsernames()).thenReturn(usernames);
        }
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(SecurityJwtConfigs.of(new SessionConfigs(cron, Cron.random())));

        // Act
        this.componentUnderTest.cleanByExpiredRefreshTokens();

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        if (cron.isEnabled()) {
            verify(this.sessionRegistry).getActiveSessionsUsernames();
            verify(this.sessionRegistry).cleanByExpiredRefreshTokens(usernames);
        }
    }

    @ParameterizedTest
    @MethodSource("cronArgs")
    void enableSessionsMetadataRenewTest(Cron cron) {
        when(this.jbstProperties.getSecurityJwtConfigs()).thenReturn(SecurityJwtConfigs.of(new SessionConfigs(Cron.random(), cron)));

        // Act
        this.componentUnderTest.enableSessionsMetadataRenew();

        // Assert
        verify(this.jbstProperties).getSecurityJwtConfigs();
        if (cron.isEnabled()) {
            verify(this.baseUsersSessionsService).enableUserRequestMetadataRenewCron();
        }
    }
}
