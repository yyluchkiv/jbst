package jbst.foundation.tests.converters;

import jbst.foundation.domain.databases.postgres.entities.PostgresDbUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class PostgresUserSessionConverter {

    public static Set<Boolean> toMetadataRenewCron(List<PostgresDbUserSession> sessions) {
        return sessions.stream().map(PostgresDbUserSession::isMetadataRenewCron).collect(Collectors.toSet());
    }
}
