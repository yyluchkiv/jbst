package jbst.foundation.validators.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstUsersTokensRepository;
import jbst.foundation.validators.abtracts.AbstractJbstUsersTokensValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoJbstUsersTokensValidator extends AbstractJbstUsersTokensValidator {

    @Autowired
    public MongoJbstUsersTokensValidator(
            MongoJbstUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
