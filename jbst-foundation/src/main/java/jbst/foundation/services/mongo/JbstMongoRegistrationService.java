package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersTokensRepository;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.services.abstracts.JbstAbstractRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoRegistrationService extends JbstAbstractRegistrationService {

    @Autowired
    public JbstMongoRegistrationService(
            JbstUsersEmailsService usersEmailsService,
            JbstMongoInvitationsRepository invitationsRepository,
            JbstMongoUsersRepository usersRepository,
            JbstMongoUsersTokensRepository usersTokensRepository,
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
