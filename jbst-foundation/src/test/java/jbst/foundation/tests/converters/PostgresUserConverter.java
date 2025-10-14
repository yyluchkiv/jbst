package jbst.foundation.tests.converters;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class PostgresUserConverter {

    public static List<String> toUsernamesAsStrings1(List<PostgresDbUser> users) {
        return Username.asStrings(users.stream().map(PostgresDbUser::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toUsernamesAsStrings2(List<PostgresDbUserSession> sessions) {
        return Username.asStrings(sessions.stream().map(PostgresDbUserSession::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toAccessTokensAsStrings2(List<PostgresDbUserSession> sessions) {
        return sessions.stream().map(session -> session.getAccessToken().value()).collect(Collectors.toList());
    }
}
