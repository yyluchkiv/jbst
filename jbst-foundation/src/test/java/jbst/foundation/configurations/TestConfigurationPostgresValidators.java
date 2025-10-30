package jbst.foundation.configurations;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({
        "jbst.foundation.validators.postgres"
})
@Import({
        TestJbstConfigurationPropertiesHardcoded.class
})
public class TestConfigurationPostgresValidators {

    // =================================================================================================================
    // Publishers
    // =================================================================================================================
    @Bean
    JbstEventsPublisher securityJwtPublisher() {
        return mock(JbstEventsPublisher.class);
    }

    @Bean
    JbstIncidentsPublisher securityJwtIncidentPublisher() {
        return mock(JbstIncidentsPublisher.class);
    }

    // =================================================================================================================
    // Repositories
    // =================================================================================================================
    @Bean
    PostgresJbstInvitationsRepository invitationsRepository() {
        return mock(PostgresJbstInvitationsRepository.class);
    }

    @Bean
    PostgresJbstUsersRepository userRepository() {
        return mock(PostgresJbstUsersRepository.class);
    }

    @Bean
    PostgresJbstUsersSessionsRepository userSessionRepository() {
        return mock(PostgresJbstUsersSessionsRepository.class);
    }
}
