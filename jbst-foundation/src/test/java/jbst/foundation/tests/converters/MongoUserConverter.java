package jbst.foundation.tests.converters;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.databases.mongo.MongoDbUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class MongoUserConverter {

    public static List<String> toUsernamesAsStrings1(List<MongoDbUser> users) {
        return Username.asStrings(users.stream().map(MongoDbUser::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toUsernamesAsStrings2(List<MongoDbUserSession> sessions) {
        return Username.asStrings(sessions.stream().map(MongoDbUserSession::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toAccessTokensAsStrings2(List<MongoDbUserSession> sessions) {
        return sessions.stream().map(session -> session.getAccessToken().value()).collect(Collectors.toList());
    }
}
