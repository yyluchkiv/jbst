package jbst.foundation.validators.mongodb;

import jbst.foundation.events.publishers.events.SecurityJwtEventsPublisher;
import jbst.foundation.events.publishers.incidents.SecurityJwtIncidentsPublisher;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractBaseRegistrationRequestsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoBaseRegistrationRequestsValidator extends AbstractBaseRegistrationRequestsValidator {

    @Autowired
    public MongoBaseRegistrationRequestsValidator(
            SecurityJwtEventsPublisher securityJwtEventsPublisher,
            SecurityJwtIncidentsPublisher securityJwtIncidentsPublisher,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                securityJwtEventsPublisher,
                securityJwtIncidentsPublisher,
                invitationsRepository,
                usersRepository
        );
    }
}
