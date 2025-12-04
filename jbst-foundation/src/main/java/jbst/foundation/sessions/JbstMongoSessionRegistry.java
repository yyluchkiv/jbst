package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.JbstMongoUsersSessionsRepository;
import jbst.foundation.services.mongo.JbstMongoUsersSessionsService;

public class JbstMongoSessionRegistry extends JbstAbstractSessionRegistry {

    public JbstMongoSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstMongoUsersSessionsService usersSessionsService,
            JbstMongoUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
