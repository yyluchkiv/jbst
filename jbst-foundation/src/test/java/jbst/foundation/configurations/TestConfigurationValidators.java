package jbst.foundation.configurations;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
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
        TestJbstConfigurationPropertiesFixed.class
})
public class TestConfigurationValidators {

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
