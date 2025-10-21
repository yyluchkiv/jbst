package jbst.foundation.configurations;

import jakarta.annotation.PostConstruct;
import jbst.foundation.assistants.userdetails.MongoUserDetailsAssistant;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstSettingsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.mongo.MongoJbstUsersSessionsService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.sessions.MongoSessionRegistry;
import jbst.foundation.settings.MongoJbstSettingsService;
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
    private final MongoJbstSettingsRepository mongoJbstSettingsRepository;
    private final MongoJbstInvitationsRepository mongoJbstInvitationsRepository;
    private final MongoJbstUsersRepository mongoUsersRepository;
    private final MongoJbstUsersSessionsRepository mongoUsersSessionsRepository;
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getMongodbSecurityJwtConfigs().assertPropertyTree();
    }

    @Bean
    MongoJbstSettingsService mongoJbstSettingsService() {
        return new MongoJbstSettingsService(
                this.mongoJbstSettingsRepository,
                this.mongoJbstInvitationsRepository,
                this.mongoUsersRepository,
                this.jbstProperties
        );
    }

    @Bean
    MongoUserDetailsAssistant mongoUserDetailsAssistant() {
        return new MongoUserDetailsAssistant(
                this.mongoUsersRepository
        );
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    JbstSessionRegistry mongoSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoJbstUsersSessionsService mongoBaseUsersSessionsService
    ) {
        return new MongoSessionRegistry(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                mongoBaseUsersSessionsService,
                this.mongoUsersSessionsRepository
        );
    }
}
