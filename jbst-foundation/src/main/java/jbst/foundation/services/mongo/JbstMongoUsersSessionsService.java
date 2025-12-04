package jbst.foundation.services.mongo;

import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.repositories.mongo.JbstMongoUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersSessionsService;
import jbst.foundation.utils.JbstGeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoUsersSessionsService extends JbstAbstractUsersSessionsService {

    @Autowired
    public JbstMongoUsersSessionsService(
            JbstEventsPublisher eventsPublisher,
            JbstMongoUsersSessionsRepository usersSessionsRepository,
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
