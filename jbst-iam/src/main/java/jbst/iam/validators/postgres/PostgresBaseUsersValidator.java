package jbst.iam.validators.postgres;

import jbst.foundation.repositories.postgres.PostgresUsersRepository;
import jbst.iam.validators.abtracts.AbstractBaseUsersValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresBaseUsersValidator extends AbstractBaseUsersValidator {

    @Autowired
    public PostgresBaseUsersValidator(
            PostgresUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
