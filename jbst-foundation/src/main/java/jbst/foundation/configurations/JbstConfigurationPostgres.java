package jbst.foundation.configurations;

import jbst.foundation.assistants.userdetails.PostgresUserDetailsAssistant;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.essense.PostgresBaseEssenceConstructor;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstSettingsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresBaseUsersSessionsService;
import jbst.foundation.sessions.PostgresSessionRegistry;
import jbst.foundation.settings.PostgresJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "jbst.foundation.services.postgres",
        "jbst.foundation.validators.postgres",
})
@Import({
        JbstConfigurationPostgresRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationPostgres {

    // Repositories
    private final PostgresJbstSettingsRepository postgresJbstSettingsRepository;
    private final PostgresJbstInvitationsRepository postgresJbstInvitationsRepository;
    private final PostgresJbstUsersRepository postgresUsersRepository;
    private final PostgresJbstUsersSessionsRepository postgresUsersSessionsRepository;
    // Properties
    private final JbstProperties jbstProperties;

    @Bean
    PostgresJbstSettingsService postgresJbstSettingsService() {
        return new PostgresJbstSettingsService(
                this.postgresJbstSettingsRepository
        );
    }

    @Bean
    PostgresUserDetailsAssistant postgresUserDetailsAssistant() {
        return new PostgresUserDetailsAssistant(
                this.postgresUsersRepository
        );
    }

    @Bean
    PostgresBaseEssenceConstructor postgresBaseEssenceConstructor() {
        return new PostgresBaseEssenceConstructor(
                this.postgresJbstInvitationsRepository,
                this.postgresUsersRepository,
                this.jbstProperties
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    PostgresSessionRegistry postgresSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresBaseUsersSessionsService postgresBaseUsersSessionsService
    ) {
        return new PostgresSessionRegistry(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                postgresBaseUsersSessionsService,
                this.postgresUsersSessionsRepository
        );
    }
}
