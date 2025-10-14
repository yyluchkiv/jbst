package jbst.foundation.services.mongodb;

import jbst.foundation.repositories.mongo.MongoUsersRepository;
import jbst.foundation.repositories.mongo.MongoUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoBaseUsersTokensService extends AbstractBaseUsersTokensService {

    @Autowired
    public MongoBaseUsersTokensService(
            MongoUsersTokensRepository usersTokensRepository,
            MongoUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
