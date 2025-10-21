package jbst.foundation.validators.postgres;

import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.validators.abtracts.AbstractJbstUsersValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostgresJbstUsersValidator extends AbstractJbstUsersValidator {

    @Autowired
    public PostgresJbstUsersValidator(
            PostgresJbstUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
