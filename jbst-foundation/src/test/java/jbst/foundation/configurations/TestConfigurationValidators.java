package jbst.foundation.configurations;

import jbst.foundation.events.publishers.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.repositories.JbstInvitationsRepository;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersSessionsRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import({
        TestJbstConfigurationPropertiesHardcoded.class
})
public class TestConfigurationValidators {

    // =================================================================================================================
    // Publishers
    // =================================================================================================================
    @Bean
    SecurityJwtEventsPublisher securityJwtPublisher() {
        return mock(SecurityJwtEventsPublisher.class);
    }

    @Bean
    SecurityJwtIncidentsPublisher securityJwtIncidentPublisher() {
        return mock(SecurityJwtIncidentsPublisher.class);
    }

    @Bean
    IncidentPublisher incidentPublisher() {
        return mock(IncidentPublisher.class);
    }

    // =================================================================================================================
    // Repositories
    // =================================================================================================================
    @Bean
    JbstInvitationsRepository invitationsRepository() {
        return mock(JbstInvitationsRepository.class);
    }

    @Bean
    JbstUsersRepository userRepository() {
        return mock(JbstUsersRepository.class);
    }

    @Bean
    JbstUsersSessionsRepository userSessionRepository() {
        return mock(JbstUsersSessionsRepository.class);
    }

    @Bean
    JbstUsersTokensRepository usersTokensRepository() {
        return mock(JbstUsersTokensRepository.class);
    }
}
