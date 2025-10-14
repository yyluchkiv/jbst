package jbst.iam.services.mongodb;

import jbst.foundation.repositories.mongo.MongoInvitationsRepository;
import jbst.iam.services.abstracts.AbstractBaseInvitationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jbst.foundation.domain.properties.JbstProperties;

@Service
public class MongoBaseInvitationsService extends AbstractBaseInvitationsService {

    @Autowired
    public MongoBaseInvitationsService(
            MongoInvitationsRepository invitationsRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                jbstProperties
        );
    }
}
