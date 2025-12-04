package jbst.foundation.tests.converters;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.mongo.JbstMongoUser;
import jbst.foundation.domain.databases.mongo.JbstMongoUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class MongoUserConverter {

    public static List<String> toUsernamesAsStrings1(List<JbstMongoUser> users) {
        return Username.asStrings(users.stream().map(JbstMongoUser::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toUsernamesAsStrings2(List<JbstMongoUserSession> sessions) {
        return Username.asStrings(sessions.stream().map(JbstMongoUserSession::getUsername).collect(Collectors.toList()));
    }

    public static List<String> toAccessTokensAsStrings2(List<JbstMongoUserSession> sessions) {
        return sessions.stream().map(session -> session.getAccessToken().value()).collect(Collectors.toList());
    }
}
