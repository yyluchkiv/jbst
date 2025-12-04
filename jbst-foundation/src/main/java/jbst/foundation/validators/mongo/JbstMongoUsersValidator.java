package jbst.foundation.validators.mongo;

import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.validators.abtracts.JbstAbstractUsersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JbstMongoUsersValidator extends JbstAbstractUsersValidator {

    @Autowired
    public JbstMongoUsersValidator(
            JbstMongoUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
