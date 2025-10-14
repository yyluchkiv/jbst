package jbst.foundation.validators.mongodb;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.MongoInvitationsRepository;
import jbst.foundation.validators.abtracts.AbstractBaseInvitationsRequestsValidator;
import org.springframework.stereotype.Component;

@Component
public class MongoBaseInvitationsRequestsValidator extends AbstractBaseInvitationsRequestsValidator {

    public MongoBaseInvitationsRequestsValidator(
            MongoInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
