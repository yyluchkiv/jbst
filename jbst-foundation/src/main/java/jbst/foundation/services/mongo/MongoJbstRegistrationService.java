package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.services.abstracts.AbstractJbstRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstRegistrationService extends AbstractJbstRegistrationService {

    @Autowired
    public MongoJbstRegistrationService(
            JbstUsersEmailsService usersEmailsService,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository,
            MongoJbstUsersTokensRepository usersTokensRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersEmailsService,
                invitationsRepository,
                usersRepository,
                usersTokensRepository,
                bCryptPasswordEncoder
        );
    }
}
