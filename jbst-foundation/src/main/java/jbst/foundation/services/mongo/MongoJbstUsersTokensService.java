package jbst.foundation.services.mongo;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractJbstUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstUsersTokensService extends AbstractJbstUsersTokensService {

    @Autowired
    public MongoJbstUsersTokensService(
            MongoJbstUsersTokensRepository usersTokensRepository,
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
