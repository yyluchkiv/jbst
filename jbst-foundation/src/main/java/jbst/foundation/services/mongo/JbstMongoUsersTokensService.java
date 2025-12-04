package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoUsersTokensService extends JbstAbstractUsersTokensService {

    @Autowired
    public JbstMongoUsersTokensService(
            JbstMongoUsersTokensRepository usersTokensRepository,
            JbstMongoUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
