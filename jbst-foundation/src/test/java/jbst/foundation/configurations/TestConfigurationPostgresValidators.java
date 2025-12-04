package jbst.foundation.configurations;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
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
    JbstEventsPublisher eventsPublisher() {
        return mock(JbstEventsPublisher.class);
    }

    @Bean
    JbstIncidentsPublisher incidentsPublisher() {
        return mock(JbstIncidentsPublisher.class);
    }

    // =================================================================================================================
    // Repositories
    // =================================================================================================================
    @Bean
    JbstPostgresInvitationsRepository invitationsRepository() {
        return mock(JbstPostgresInvitationsRepository.class);
    }

    @Bean
    JbstPostgresUsersRepository userRepository() {
        return mock(JbstPostgresUsersRepository.class);
    }

    @Bean
    JbstPostgresUsersSessionsRepository userSessionRepository() {
        return mock(JbstPostgresUsersSessionsRepository.class);
    }
}
