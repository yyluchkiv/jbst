package jbst.foundation.integration.postgres.configs;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainerV11 extends PostgreSQLContainer<PostgresContainerV11> {
    // WARNING: ubuntu-based issue https://github.com/docker-library/postgres/issues/1046
    private static final String POSTGRES_DB_VERSION = "postgres:11.1";
    private static final String POSTGRES_DATABASE_NAME = "integration-tests-db";
    private static final String POSTGRES_USERNAME = "sa";
    private static final String POSTGRES_PASSWORD = "sa";
    public static final String POSTGRES_INIT_SQL = "database/v001_jbst_schema.sql";

    public static PostgresContainerV11 container = new PostgresContainerV11()
            .withDatabaseName(POSTGRES_DATABASE_NAME)
            .withUsername(POSTGRES_USERNAME)
            .withPassword(POSTGRES_PASSWORD)
            .withInitScript(POSTGRES_INIT_SQL)
            .withReuse(true);

    private PostgresContainerV11() {
        super(POSTGRES_DB_VERSION);
    }
}
