package jbst.foundation.services.postgres;

import jbst.foundation.utils.JbstSecurityUtils;
import jbst.foundation.utils.UserMetadataUtils;
import jbst.iam.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.repositories.postgres.PostgresUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersSessionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresBaseUsersSessionsService extends AbstractBaseUsersSessionsService {

    @Autowired
    public PostgresBaseUsersSessionsService(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            PostgresUsersSessionsRepository usersSessionsRepository,
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
