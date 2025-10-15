package jbst.foundation.services.mongodb;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.services.abstracts.AbstractBaseInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseInvitationsService extends AbstractBaseInvitationsService {

    @Autowired
    public MongoBaseInvitationsService(
            MongoJbstInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
