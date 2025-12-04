package jbst.foundation.tests.converters;

import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class PostgresUserSessionConverter {

    public static Set<Boolean> toMetadataRenewCron(List<JbstPostgresUserSession> sessions) {
        return sessions.stream().map(JbstPostgresUserSession::isMetadataRenewCron).collect(Collectors.toSet());
    }
}
