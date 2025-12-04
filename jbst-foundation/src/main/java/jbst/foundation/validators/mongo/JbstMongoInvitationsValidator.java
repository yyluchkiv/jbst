package jbst.foundation.validators.mongo;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.validators.abtracts.JbstAbstractInvitationsValidator;
import org.springframework.stereotype.Component;

@Component
public class JbstMongoInvitationsValidator extends JbstAbstractInvitationsValidator {

    public JbstMongoInvitationsValidator(
            JbstMongoInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
