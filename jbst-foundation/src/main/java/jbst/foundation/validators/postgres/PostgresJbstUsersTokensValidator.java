package jbst.foundation.validators.postgres;

import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.validators.abtracts.AbstractJbstUsersTokensValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostgresJbstUsersTokensValidator extends AbstractJbstUsersTokensValidator {

    @Autowired
    public PostgresJbstUsersTokensValidator(
            PostgresJbstUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
