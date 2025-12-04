package jbst.foundation.services.postgres;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstPostgresUsersSessionsService extends JbstAbstractUsersSessionsService {

    @Autowired
    public JbstPostgresUsersSessionsService(
            JbstEventsPublisher eventsPublisher,
            JbstPostgresUsersSessionsRepository usersSessionsRepository,
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
