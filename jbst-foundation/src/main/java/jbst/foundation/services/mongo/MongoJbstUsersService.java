package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractJbstUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstUsersService extends AbstractJbstUsersService {

    @Autowired
    public MongoJbstUsersService(
            MongoJbstUsersTokensRepository usersTokensRepository,
            MongoJbstUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
