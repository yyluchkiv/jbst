package jbst.foundation.tests.converters;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PostgresUserConverter {

    public static List<String> toUsernamesAsStrings1(List<JbstPostgresUser> users) {
        return Username.asStrings(users.stream().map(JbstPostgresUser::getUsername).toList());
    }

    public static List<String> toUsernamesAsStrings2(List<JbstPostgresUserSession> sessions) {
        return Username.asStrings(sessions.stream().map(JbstPostgresUserSession::getUsername).toList());
    }

    public static List<String> toAccessTokensAsStrings2(List<JbstPostgresUserSession> sessions) {
        return sessions.stream().map(session -> session.getAccessToken().value()).toList();
    }
}
