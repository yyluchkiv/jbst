package jbst.foundation.validators.mongodb;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.validators.abtracts.AbstractBaseInvitationsRequestsValidator;
import org.springframework.stereotype.Component;

@Component
public class MongoBaseInvitationsRequestsValidator extends AbstractBaseInvitationsRequestsValidator {

    public MongoBaseInvitationsRequestsValidator(
            MongoJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
