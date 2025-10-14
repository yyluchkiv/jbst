package jbst.iam.services.postgres;

import jbst.foundation.repositories.postgres.PostgresInvitationsRepository;
import jbst.iam.services.abstracts.AbstractBaseInvitationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.foundation.domain.properties.JbstProperties;

@Slf4j
@Service
public class PostgresBaseInvitationsService extends AbstractBaseInvitationsService {

    @Autowired
    public PostgresBaseInvitationsService(
            PostgresInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
