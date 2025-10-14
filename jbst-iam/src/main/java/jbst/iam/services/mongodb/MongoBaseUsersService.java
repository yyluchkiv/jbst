package jbst.iam.services.mongodb;

import jbst.foundation.repositories.mongo.MongoUsersRepository;
import jbst.foundation.repositories.mongo.MongoUsersTokensRepository;
import jbst.iam.services.abstracts.AbstractBaseUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseUsersService extends AbstractBaseUsersService {

    @Autowired
    public MongoBaseUsersService(
            MongoUsersTokensRepository usersTokensRepository,
            MongoUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
