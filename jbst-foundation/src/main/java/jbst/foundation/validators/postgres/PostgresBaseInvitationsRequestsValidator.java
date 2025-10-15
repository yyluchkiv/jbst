package jbst.foundation.validators.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.validators.abtracts.AbstractBaseInvitationsRequestsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresBaseInvitationsRequestsValidator extends AbstractBaseInvitationsRequestsValidator {

    @Autowired
    public PostgresBaseInvitationsRequestsValidator(
            PostgresJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
