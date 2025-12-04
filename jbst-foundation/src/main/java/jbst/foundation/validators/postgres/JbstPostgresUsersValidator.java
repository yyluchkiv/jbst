package jbst.foundation.validators.postgres;

import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.validators.abtracts.JbstAbstractUsersValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JbstPostgresUsersValidator extends JbstAbstractUsersValidator {

    @Autowired
    public JbstPostgresUsersValidator(
            JbstPostgresUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }

}
