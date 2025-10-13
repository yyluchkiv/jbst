package jbst.iam.postgres.configs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.data.jpa.repository.JpaRepository;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TestsJbstConfigurationPostgresRepositoriesRunner {

    @AfterEach
    void afterEach() {
        this.getJpaRepository().deleteAll();
    }

    public abstract JpaRepository<?, String> getJpaRepository();
}
