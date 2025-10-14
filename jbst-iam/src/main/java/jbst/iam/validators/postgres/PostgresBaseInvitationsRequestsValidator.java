package jbst.iam.validators.postgres;

import jbst.foundation.repositories.postgres.PostgresInvitationsRepository;
import jbst.iam.validators.abtracts.AbstractBaseInvitationsRequestsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jbst.foundation.domain.properties.JbstProperties;

@Slf4j
@Component
public class PostgresBaseInvitationsRequestsValidator extends AbstractBaseInvitationsRequestsValidator {

    @Autowired
    public PostgresBaseInvitationsRequestsValidator(
            PostgresInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
