package jbst.foundation.services.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.services.UsersEmailsService;
import jbst.foundation.services.abstracts.AbstractBaseRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseRegistrationService extends AbstractBaseRegistrationService {

    @Autowired
    public MongoBaseRegistrationService(
            UsersEmailsService usersEmailsService,
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
