package jbst.foundation.tests.converters;

import jbst.foundation.domain.databases.mongo.JbstMongoUserSession;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class MongoUserSessionConverter {

    public static Set<Boolean> toMetadataRenewCron(List<JbstMongoUserSession> sessions) {
        return sessions.stream().map(JbstMongoUserSession::isMetadataRenewCron).collect(Collectors.toSet());
    }
}
