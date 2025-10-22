package jbst.foundation.services.postgres;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresJbstUsersSessionsService extends AbstractJbstUsersSessionsService {

    @Autowired
    public PostgresJbstUsersSessionsService(
            JbstEventsPublisher eventsPublisher,
            PostgresJbstUsersSessionsRepository usersSessionsRepository,
            JbstGeoUtils geoUtils,
            JbstSecurityUtils securityUtils
    ) {
        super(
                eventsPublisher,
                usersSessionsRepository,
                geoUtils,
                securityUtils
        );
    }
}
