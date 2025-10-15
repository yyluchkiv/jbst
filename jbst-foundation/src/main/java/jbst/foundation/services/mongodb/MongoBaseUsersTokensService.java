package jbst.foundation.services.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseUsersTokensService extends AbstractBaseUsersTokensService {

    @Autowired
    public MongoBaseUsersTokensService(
            MongoJbstUsersTokensRepository usersTokensRepository,
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
