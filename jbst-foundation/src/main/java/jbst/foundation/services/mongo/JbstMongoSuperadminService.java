package jbst.foundation.services.mongo;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.JbstAbstractTaskOnResetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstMongoSuperadminService extends JbstAbstractSuperadminService {

    @Autowired
    public JbstMongoSuperadminService(
            JbstIncidentsPublisher incidentsPublisher,
            JbstSessionRegistry sessionRegistry,
            JbstMongoInvitationsRepository invitationsRepository,
            JbstMongoUsersRepository usersRepository,
            JbstMongoUsersSessionsRepository usersSessionsRepository,
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
