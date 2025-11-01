package jbst.foundation.validators.postgres;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractJbstRegistrationValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresJbstRegistrationValidator extends AbstractJbstRegistrationValidator {

    @Autowired
    public PostgresJbstRegistrationValidator(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersRepository usersRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
