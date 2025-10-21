package jbst.foundation.validators.postgres;

import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
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
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersRepository usersRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
