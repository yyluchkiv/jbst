package jbst.foundation.services.postgres;

import jbst.foundation.incidents.events.publishers.IncidentPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractBaseSuperadminService;
import jbst.foundation.sessions.JbstSessionRegistry;
import jbst.foundation.tasks.AbstractSuperAdminResetServerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresBaseSuperadminService extends AbstractBaseSuperadminService {

    @Autowired
    public PostgresBaseSuperadminService(
            IncidentPublisher incidentPublisher,
            JbstSessionRegistry sessionRegistry,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersSessionsRepository usersSessionsRepository,
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
