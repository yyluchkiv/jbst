package jbst.foundation.validators.mongo;

import jbst.foundation.events.publishers.JbstEventsPublisher;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractJbstRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoJbstRegistrationValidator extends AbstractJbstRegistrationValidator {

    @Autowired
    public MongoJbstRegistrationValidator(
            JbstEventsPublisher eventsPublisher,
            JbstIncidentsPublisher incidentsPublisher,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                eventsPublisher,
                incidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
