package jbst.foundation.validators.mongodb;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractJbstUsersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoJbstUsersValidator extends AbstractJbstUsersValidator {

    @Autowired
    public MongoJbstUsersValidator(
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
