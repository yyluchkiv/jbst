package jbst.foundation.services.postgres;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.postgres.JbstPostgresInvitationsRepository;
import jbst.foundation.services.abstracts.JbstAbstractInvitationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstPostgresInvitationsService extends JbstAbstractInvitationsService {

    @Autowired
    public JbstPostgresInvitationsService(
            JbstPostgresInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
