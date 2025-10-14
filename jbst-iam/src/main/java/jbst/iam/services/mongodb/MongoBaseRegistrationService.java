package jbst.iam.services.mongodb;

import jbst.foundation.repositories.mongo.MongoInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoUsersRepository;
import jbst.iam.services.abstracts.AbstractBaseRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseRegistrationService extends AbstractBaseRegistrationService {

    @Autowired
    public MongoBaseRegistrationService(
            MongoInvitationsRepository invitationsRepository,
            MongoUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                invitationsRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
