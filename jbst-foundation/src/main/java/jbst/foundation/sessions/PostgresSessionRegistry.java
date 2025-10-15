package jbst.foundation.sessions;

import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresBaseUsersSessionsService;

public class PostgresSessionRegistry extends AbstractJbstSessionRegistry {

    public PostgresSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresBaseUsersSessionsService baseUsersSessionsService,
            PostgresJbstUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                baseUsersSessionsService,
                usersSessionsRepository
        );
    }
}
