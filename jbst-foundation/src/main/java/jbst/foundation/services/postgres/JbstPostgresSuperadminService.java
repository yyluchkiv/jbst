package jbst.foundation.services.postgres;

import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.JbstAbstractTaskOnResetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstPostgresSuperadminService extends JbstAbstractSuperadminService {

    @Autowired
    public JbstPostgresSuperadminService(
            JbstIncidentsPublisher incidentsPublisher,
            JbstSessionRegistry sessionRegistry,
            JbstPostgresInvitationsRepository invitationsRepository,
            JbstPostgresUsersRepository usersRepository,
            JbstPostgresUsersSessionsRepository usersSessionsRepository,
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
