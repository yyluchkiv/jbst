package jbst.foundation.configurations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import jbst.foundation.domain.converters.JbstMongoConverters;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        JbstProperties.class
})
@EntityScan({
        "jbst.foundation.domain.databases.mongo"
})
@EnableMongoRepositories(
        basePackages = "jbst.foundation.repositories",
        mongoTemplateRef = "jbstMongoTemplate"
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstConfigurationMongoRepositories {
    // Properties
    private final JbstProperties jbstProperties;

    @PostConstruct
    public void init() {
        this.jbstProperties.getDatabases().getMongo().assertProperties();
    }

    @Bean
    public JbstMongoRepositories jbstMongoRepositories(
            JbstMongoSettingsRepository settingsRepository,
            JbstMongoInvitationsRepository invitationsRepository,
            JbstMongoUsersTokensRepository usersTokensRepository,
            JbstMongoUsersRepository usersRepository,
            JbstMongoUsersSessionsRepository userSessionRepository
    ) {
        return new JbstMongoRepositories(
                settingsRepository,
                invitationsRepository,
                usersTokensRepository,
                usersRepository,
                userSessionRepository
        );
    }

    @Bean
    public MongoClient jbstMongoClient() {
        var connectionString = new ConnectionString(this.jbstProperties.getDatabases().getMongo().getDatabase().connectionString());
        var mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoDatabaseFactory jbstMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(
                this.jbstMongoClient(),
                this.jbstProperties.getDatabases().getMongo().getDatabase().getName()
        );
    }

    @Bean
    public MongoTemplate jbstMongoTemplate() {
        var customConversions = new MongoCustomConversions(List.of(
                JbstMongoConverters.SimpleGrantedAuthorityReadConverter.INSTANCE,
                JbstMongoConverters.SimpleGrantedAuthorityWriteConverter.INSTANCE
        ));
        var mappingContext = new MongoMappingContext();
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.afterPropertiesSet();
        var dbRefResolver = new DefaultDbRefResolver(this.jbstMongoDatabaseFactory());
        var mongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        mongoConverter.setCustomConversions(customConversions);
        mongoConverter.afterPropertiesSet();
        return new MongoTemplate(
                this.jbstMongoDatabaseFactory(),
                mongoConverter
        );
    }
}
