package jbst.iam.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.iam.assistants.userdetails.PostgresUserDetailsAssistant;
import jbst.iam.essence.PostgresBaseEssenceConstructor;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.iam.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.iam.repositories.postgres.PostgresInvitationsRepository;
import jbst.iam.repositories.postgres.PostgresJbstSettingsRepository;
import jbst.iam.repositories.postgres.PostgresUsersRepository;
import jbst.iam.repositories.postgres.PostgresUsersSessionsRepository;
import jbst.iam.services.postgres.PostgresBaseUsersSessionsService;
import jbst.iam.sessions.PostgresSessionRegistry;
import jbst.iam.settings.PostgresJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "jbst.iam.services.postgres",
        "jbst.iam.validators.postgres",
})
@Import({
        JbstConfigurationPostgresRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationPostgres {

    // Repositories
    private final PostgresJbstSettingsRepository postgresJbstSettingsRepository;
    private final PostgresInvitationsRepository postgresInvitationsRepository;
    private final PostgresUsersRepository postgresUsersRepository;
    private final PostgresUsersSessionsRepository postgresUsersSessionsRepository;
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
                this.postgresInvitationsRepository,
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
