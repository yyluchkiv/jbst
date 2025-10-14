package jbst.foundation.sessions;

import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresUsersSessionsRepository;
import jbst.foundation.services.postgres.PostgresBaseUsersSessionsService;

public class PostgresSessionRegistry extends AbstractSessionRegistry {

    public PostgresSessionRegistry(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresBaseUsersSessionsService baseUsersSessionsService,
            PostgresUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                baseUsersSessionsService,
                usersSessionsRepository
        );
    }
}
