package jbst.foundation.services.mongo;

import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.AbstractJbstResetServerTask;
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
            AbstractJbstResetServerTask resetServerTask
    ) {
        super(
                incidentsPublisher,
                sessionRegistry,
                invitationsRepository,
                usersRepository,
                usersSessionsRepository,
                resetServerTask
        );
    }
}
