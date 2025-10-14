package jbst.foundation.repositories.mongo.configs;

import jbst.foundation.domain.properties.JbstProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.MongoRepository;

@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableConfigurationProperties(
        JbstProperties.class
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TestsJbstConfigurationMongoRepositoriesRunner {

    @AfterEach
    void afterEach() {
        this.getMongoRepository().deleteAll();
    }

    public abstract MongoRepository<?, String> getMongoRepository();
}
