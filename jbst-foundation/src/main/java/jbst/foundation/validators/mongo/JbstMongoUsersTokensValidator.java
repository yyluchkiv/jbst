package jbst.foundation.validators.mongo;

import jbst.foundation.repositories.mongo.JbstMongoUsersTokensRepository;
import jbst.foundation.validators.abtracts.JbstAbstractUsersTokensValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JbstMongoUsersTokensValidator extends JbstAbstractUsersTokensValidator {

    @Autowired
    public JbstMongoUsersTokensValidator(
            JbstMongoUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
