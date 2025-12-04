package jbst.foundation.services.mongo;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.services.abstracts.JbstAbstractInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoInvitationsService extends JbstAbstractInvitationsService {

    @Autowired
    public JbstMongoInvitationsService(
            JbstMongoInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
