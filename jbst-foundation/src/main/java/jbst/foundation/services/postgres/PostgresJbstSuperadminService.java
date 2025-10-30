package jbst.foundation.services.postgres;

import jbst.foundation.events.publishers.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.AbstractJbstResetServerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresJbstSuperadminService extends AbstractJbstSuperadminService {

    @Autowired
    public PostgresJbstSuperadminService(
            JbstIncidentsPublisher incidentsPublisher,
            JbstSessionRegistry sessionRegistry,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersRepository usersRepository,
            PostgresJbstUsersSessionsRepository usersSessionsRepository,
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
