package jbst.foundation.services.mongo;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.JbstAbstractTaskOnResetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MongoJbstSuperadminService extends AbstractJbstSuperadminService {

    @Autowired
    public MongoJbstSuperadminService(
            JbstIncidentsPublisher incidentsPublisher,
            JbstSessionRegistry sessionRegistry,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository,
            MongoJbstUsersSessionsRepository usersSessionsRepository,
            JbstAbstractTaskOnResetServer taskOnResetServer
    ) {
        super(
                incidentsPublisher,
                sessionRegistry,
                invitationsRepository,
                usersRepository,
                usersSessionsRepository,
                taskOnResetServer
        );
    }
}
