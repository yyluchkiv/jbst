package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.mongo.MongoJbstUsersSessionsService;

public class MongoSessionRegistry extends AbstractJbstSessionRegistry {

    public MongoSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoJbstUsersSessionsService usersSessionsService,
            MongoJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                securityJwtIncidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
