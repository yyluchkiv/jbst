package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresJbstUsersSessionsService;

public class PostgresSessionRegistry extends AbstractJbstSessionRegistry {

    public PostgresSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresJbstUsersSessionsService usersSessionsService,
            PostgresJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                securityJwtIncidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
