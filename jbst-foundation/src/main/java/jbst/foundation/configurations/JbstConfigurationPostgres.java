package jbst.foundation.configurations;

import jbst.foundation.assistants.userdetails.JbstPostgresUserDetailsService;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresSettingsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
import jbst.foundation.services.postgres.JbstPostgresUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.sessions.JbstPostgresSessionRegistry;
import jbst.foundation.settings.JbstPostgresSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "jbst.foundation.services.postgres",
        "jbst.foundation.validators.postgres"
})
@Import({
        JbstConfigurationPostgresRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationPostgres {

    // Repositories
    private final JbstPostgresSettingsRepository jbstPostgresSettingsRepository;
    private final JbstPostgresUsersRepository postgresUsersRepository;
    private final JbstPostgresUsersSessionsRepository postgresUsersSessionsRepository;

    @Bean
    JbstPostgresSettingsService postgresJbstSettingsService() {
        return new JbstPostgresSettingsService(
                this.jbstPostgresSettingsRepository
        );
    }

    @Bean
    JbstPostgresUserDetailsService postgresUserDetailsAssistant() {
        return new JbstPostgresUserDetailsService(
                this.postgresUsersRepository
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    JbstSessionRegistry postgresSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstPostgresUsersSessionsService postgresBaseUsersSessionsService
    ) {
        return new JbstPostgresSessionRegistry(
                eventsPublisher,
                incidentsPublisher,
                postgresBaseUsersSessionsService,
                this.postgresUsersSessionsRepository
        );
    }
}
