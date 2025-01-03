package jbst.iam.postgres.configs;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TestsJbstJbstConfigurationPostgresRepositoriesRunner {

    @AfterEach
    void afterEach() {
        this.getJpaRepository().deleteAll();
    }

    public abstract JpaRepository<?, UUID> getJpaRepository();
}
