package jbst.foundation.services.mongo;

import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.AbstractSuperAdminResetServerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MongoJbstSuperadminService extends AbstractJbstSuperadminService {

    @Autowired
    public MongoJbstSuperadminService(
            IncidentPublisher incidentPublisher,
            JbstSessionRegistry sessionRegistry,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersSessionsRepository usersSessionsRepository,
            AbstractSuperAdminResetServerTask abstractSuperAdminResetServerTask
    ) {
        super(
                incidentPublisher,
                sessionRegistry,
                invitationsRepository,
                usersSessionsRepository,
                abstractSuperAdminResetServerTask
        );
    }
}
