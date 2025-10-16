package jbst.foundation.services.postgres;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresBaseUsersSessionsService extends AbstractBaseUsersSessionsService {

    @Autowired
    public PostgresBaseUsersSessionsService(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            PostgresJbstUsersSessionsRepository usersSessionsRepository,
            JbstGeoUtils geoUtils,
            JbstSecurityUtils securityUtils
    ) {
        super(
                securityJwtEventsPublisher,
                usersSessionsRepository,
                geoUtils,
                securityUtils
        );
    }
}
