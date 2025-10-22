package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresJbstUsersSessionsService;

public class PostgresSessionRegistry extends AbstractJbstSessionRegistry {

    public PostgresSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            PostgresJbstUsersSessionsService usersSessionsService,
            PostgresJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
