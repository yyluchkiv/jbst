package jbst.foundation.validators.postgres;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.validators.abtracts.JbstAbstractRegistrationValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JbstPostgresRegistrationValidator extends JbstAbstractRegistrationValidator {

    @Autowired
    public JbstPostgresRegistrationValidator(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstPostgresInvitationsRepository invitationsRepository,
            JbstPostgresUsersRepository usersRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
