package jbst.foundation.validators.postgres;

import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.validators.abtracts.AbstractBaseUsersTokensRequestsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostgresBaseUsersTokensRequestsValidator extends AbstractBaseUsersTokensRequestsValidator {

    @Autowired
    public PostgresBaseUsersTokensRequestsValidator(
            PostgresJbstUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
