package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoUsersService extends JbstAbstractUsersService {

    @Autowired
    public JbstMongoUsersService(
            JbstMongoUsersTokensRepository usersTokensRepository,
            JbstMongoUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
