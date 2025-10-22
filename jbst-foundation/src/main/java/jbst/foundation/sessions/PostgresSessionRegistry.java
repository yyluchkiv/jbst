package jbst.foundation.sessions;

import jbst.foundation.events.publishers.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresJbstUsersSessionsService;

public class PostgresSessionRegistry extends AbstractJbstSessionRegistry {

    public PostgresSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresJbstUsersSessionsService usersSessionsService,
            PostgresJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
