package jbst.foundation.services.mongo;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.services.abstracts.AbstractJbstInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstInvitationsService extends AbstractJbstInvitationsService {

    @Autowired
    public MongoJbstInvitationsService(
            MongoJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
