package jbst.foundation.services.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.services.abstracts.AbstractJbstInvitationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresJbstInvitationsService extends AbstractJbstInvitationsService {

    @Autowired
    public PostgresJbstInvitationsService(
            PostgresJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
