package jbst.foundation.validators.postgres;

import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.validators.abtracts.JbstAbstractUsersTokensValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JbstPostgresUsersTokensValidator extends JbstAbstractUsersTokensValidator {

    @Autowired
    public JbstPostgresUsersTokensValidator(
            JbstPostgresUsersTokensRepository usersTokensRepository
    ) {
        super(
                usersTokensRepository
        );
    }
}
