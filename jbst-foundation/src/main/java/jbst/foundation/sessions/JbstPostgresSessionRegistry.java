package jbst.foundation.sessions;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
import jbst.foundation.services.postgres.JbstPostgresUsersSessionsService;

public class JbstPostgresSessionRegistry extends JbstAbstractSessionRegistry {

    public JbstPostgresSessionRegistry(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstPostgresUsersSessionsService usersSessionsService,
            JbstPostgresUsersSessionsRepository usersSessionsRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                usersSessionsService,
                usersSessionsRepository
        );
    }
}
