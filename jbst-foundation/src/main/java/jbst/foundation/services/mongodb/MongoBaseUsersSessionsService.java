package jbst.foundation.services.mongodb;

import jbst.foundation.utils.JbstSecurityUtils;
import jbst.foundation.utils.UserMetadataUtils;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.mongo.MongoUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersSessionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseUsersSessionsService extends AbstractBaseUsersSessionsService {

    @Autowired
    public MongoBaseUsersSessionsService(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            MongoUsersSessionsRepository usersSessionsRepository,
            UserMetadataUtils userMetadataUtils,
            JbstSecurityUtils securityUtils
    ) {
        super(
                securityJwtEventsPublisher,
                usersSessionsRepository,
                userMetadataUtils,
                securityUtils
        );
    }
}
