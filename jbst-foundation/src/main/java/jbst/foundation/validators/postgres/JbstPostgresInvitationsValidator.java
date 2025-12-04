package jbst.foundation.validators.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.validators.abtracts.JbstAbstractInvitationsValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JbstPostgresInvitationsValidator extends JbstAbstractInvitationsValidator {

    @Autowired
    public JbstPostgresInvitationsValidator(
            JbstPostgresInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
