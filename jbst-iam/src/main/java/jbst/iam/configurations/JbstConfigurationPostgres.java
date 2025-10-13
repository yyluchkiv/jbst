package jbst.iam.configurations;

import jbst.foundation.configurations.JbstConfigurationSettingsOnInit;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
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
import jbst.iam.settings.PostgresBaseJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@ComponentScan({
        "jbst.iam.services.postgres",
        "jbst.iam.validators.postgres",
})
@Import({
        JbstConfigurationSettingsOnInit.class,
        JbstConfigurationPostgresRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationPostgres {

    // Settings
    private final JbstSettingsOnInit jbstSettingsOnInit;
    // Repositories
    private final PostgresJbstSettingsRepository postgresJbstSettingsRepository;

    @Bean
    PostgresBaseJbstSettingsService postgresBaseJbstSettingsService() {
        return new PostgresBaseJbstSettingsService(
                this.jbstSettingsOnInit,
                this.postgresJbstSettingsRepository
        );
    }

    @Bean
    PostgresUserDetailsAssistant postgresUserDetailsAssistant(
            PostgresUsersRepository postgresUsersRepository
    ) {
        return new PostgresUserDetailsAssistant(
                postgresUsersRepository
        );
    }

    @Bean
    PostgresBaseEssenceConstructor postgresBaseEssenceConstructor(
            PostgresInvitationsRepository postgresInvitationsRepository,
            PostgresUsersRepository postgresUsersRepository,
            JbstProperties jbstProperties
    ) {
        return new PostgresBaseEssenceConstructor(
                postgresInvitationsRepository,
                postgresUsersRepository,
                jbstProperties
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    PostgresSessionRegistry postgresSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresBaseUsersSessionsService postgresBaseUsersSessionsService,
            PostgresUsersSessionsRepository postgresUsersSessionsRepository
    ) {
        return new PostgresSessionRegistry(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                postgresBaseUsersSessionsService,
                postgresUsersSessionsRepository
        );
    }
}
