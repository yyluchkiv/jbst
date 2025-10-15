package jbst.foundation.sessions;

import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.mongodb.MongoBaseUsersSessionsService;

public class MongoSessionRegistry extends AbstractSessionRegistry {

    public MongoSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoBaseUsersSessionsService baseUsersSessionsService,
            MongoJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                baseUsersSessionsService,
                usersSessionsRepository
        );
    }
}
