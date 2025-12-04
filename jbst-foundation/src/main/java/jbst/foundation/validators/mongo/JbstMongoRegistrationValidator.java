package jbst.foundation.validators.mongo;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.validators.abtracts.JbstAbstractRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JbstMongoRegistrationValidator extends JbstAbstractRegistrationValidator {

    @Autowired
    public JbstMongoRegistrationValidator(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            JbstMongoInvitationsRepository invitationsRepository,
            JbstMongoUsersRepository usersRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
