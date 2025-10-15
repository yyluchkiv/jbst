package jbst.foundation.validators.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.validators.abtracts.AbstractBaseUsersTokensRequestsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoBaseUsersTokensRequestsValidator extends AbstractBaseUsersTokensRequestsValidator {

    @Autowired
    public MongoBaseUsersTokensRequestsValidator(
            MongoJbstUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
