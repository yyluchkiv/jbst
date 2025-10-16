package jbst.foundation.services.mongodb;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseUsersSessionsService extends AbstractBaseUsersSessionsService {

    @Autowired
    public MongoBaseUsersSessionsService(
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
