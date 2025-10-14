package jbst.foundation.domain.databases.postgres.projections;

import jbst.foundation.domain.base.Username;

public interface PostgresDbUserProjection1 {
    String getUsername();

    default Username getAsUsername() {
        return Username.of(this.getUsername());
    }
}
