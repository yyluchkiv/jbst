package jbst.foundation.validators.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractBaseUsersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoBaseUsersValidator extends AbstractBaseUsersValidator {

    @Autowired
    public MongoBaseUsersValidator(
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
