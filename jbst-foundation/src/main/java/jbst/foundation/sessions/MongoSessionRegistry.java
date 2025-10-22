package jbst.foundation.sessions;

import jbst.foundation.events.publishers.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.mongo.MongoJbstUsersSessionsService;

public class MongoSessionRegistry extends AbstractJbstSessionRegistry {

    public MongoSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoJbstUsersSessionsService usersSessionsService,
            MongoJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
