package jbst.foundation.configurations;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@EntityScan({
        "jbst.foundation.domain.databases.postgres"
})
@EnableJpaRepositories({
        "jbst.foundation.repositories.postgres"
})
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationPostgresRepositories {

    // Repositories
    private final JbstPostgresSettingsRepository settingsRepository;
    private final JbstPostgresInvitationsRepository invitationsRepository;
    private final JbstPostgresUsersTokensRepository usersTokensRepository;
    private final JbstPostgresUsersRepository userRepository;
    private final JbstPostgresUsersSessionsRepository userSessionRepository;

    @Bean
    public JbstPostgresRepositories jbstPostgresRepositories() {
        return new JbstPostgresRepositories(
                this.settingsRepository,
                this.invitationsRepository,
                this.usersTokensRepository,
                this.userRepository,
                this.userSessionRepository
        );
    }
}
