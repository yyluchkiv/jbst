package jbst.iam.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.configurations.JbstConfigurationSettingsOnInit;
import jbst.foundation.domain.base.PropertyId;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.JbstSettingsOnInit;
import jbst.iam.assistants.userdetails.MongoUserDetailsAssistant;
import jbst.iam.essence.MongoBaseEssenceConstructor;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.iam.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.iam.repositories.mongodb.MongoInvitationsRepository;
import jbst.iam.repositories.mongodb.MongoJbstSettingsRepository;
import jbst.iam.repositories.mongodb.MongoUsersRepository;
import jbst.iam.repositories.mongodb.MongoUsersSessionsRepository;
import jbst.iam.services.mongodb.MongoBaseUsersSessionsService;
import jbst.iam.sessions.MongoSessionRegistry;
import jbst.iam.settings.MongoJbstSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({
        "jbst.iam.services.mongodb",
        "jbst.iam.validators.mongodb",
})
@Import({
        JbstConfigurationSettingsOnInit.class,
        JbstConfigurationMongoRepositories.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationMongo {

    // Settings
    private final JbstSettingsOnInit jbstSettingsOnInit;
    // Repositories
    private final MongoJbstSettingsRepository mongoJbstSettingsRepository;
    private final MongoInvitationsRepository mongoInvitationsRepository;
    private final MongoUsersRepository mongoUsersRepository;
    private final MongoUsersSessionsRepository mongoUsersSessionsRepository;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getMongodbSecurityJwtConfigs().assertProperties(new PropertyId("mongodbSecurityJwtConfigs"));
    }

    @Bean
    MongoJbstSettingsService mongoJbstSettingsService() {
        return new MongoJbstSettingsService(
                this.mongoJbstSettingsRepository
        );
    }

    @Bean
    MongoUserDetailsAssistant mongoUserDetailsAssistant() {
        return new MongoUserDetailsAssistant(
                this.mongoUsersRepository
        );
    }

    @Bean
    MongoBaseEssenceConstructor mongoBaseEssenceConstructor() {
        return new MongoBaseEssenceConstructor(
                this.mongoInvitationsRepository,
                this.mongoUsersRepository,
                this.jbstProperties
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    MongoSessionRegistry mongoSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoBaseUsersSessionsService mongoBaseUsersSessionsService
    ) {
        return new MongoSessionRegistry(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                mongoBaseUsersSessionsService,
                this.mongoUsersSessionsRepository
        );
    }
}
