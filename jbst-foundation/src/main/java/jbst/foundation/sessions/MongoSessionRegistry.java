package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.mongo.MongoJbstUsersSessionsService;

public class MongoSessionRegistry extends AbstractJbstSessionRegistry {

    public MongoSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            MongoJbstUsersSessionsService usersSessionsService,
            MongoJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
