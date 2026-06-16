package jbst.foundation.configurations;

import jbst.foundation.assistants.userdetails.JbstMongoUserDetailsService;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.JbstMongoSettingsRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersSessionsRepository;
import jbst.foundation.services.mongo.JbstMongoUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.sessions.JbstMongoSessionRegistry;
import jbst.foundation.settings.JbstMongoSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "jbst.foundation.services.mongo",
        "jbst.foundation.validators.mongo"
})
@Import({
        JbstConfigurationMongoRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationMongo {

    // Repositories
    private final JbstMongoSettingsRepository jbstMongoSettingsRepository;
    private final JbstMongoUsersRepository mongoUsersRepository;
    private final JbstMongoUsersSessionsRepository mongoUsersSessionsRepository;

    @Bean
    JbstMongoSettingsService mongoJbstSettingsService() {
        return new JbstMongoSettingsService(
                this.jbstMongoSettingsRepository
        );
    }

    @Bean
    JbstMongoUserDetailsService mongoUserDetailsAssistant() {
        return new JbstMongoUserDetailsService(
                this.mongoUsersRepository
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    JbstSessionRegistry mongoSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstMongoUsersSessionsService mongoBaseUsersSessionsService
    ) {
        return new JbstMongoSessionRegistry(
                eventsPublisher,
                incidentsPublisher,
                mongoBaseUsersSessionsService,
                this.mongoUsersSessionsRepository
        );
    }
}
