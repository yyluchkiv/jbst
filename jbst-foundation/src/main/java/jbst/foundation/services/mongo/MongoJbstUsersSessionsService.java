package jbst.foundation.services.mongo;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstUsersSessionsService extends AbstractJbstUsersSessionsService {

    @Autowired
    public MongoJbstUsersSessionsService(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            MongoJbstUsersSessionsRepository usersSessionsRepository,
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
