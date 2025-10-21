package jbst.foundation.validators.mongo;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.validators.abtracts.AbstractJbstInvitationsValidator;
import org.springframework.stereotype.Component;

@Component
public class MongoJbstInvitationsValidator extends AbstractJbstInvitationsValidator {

    public MongoJbstInvitationsValidator(
            MongoJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
