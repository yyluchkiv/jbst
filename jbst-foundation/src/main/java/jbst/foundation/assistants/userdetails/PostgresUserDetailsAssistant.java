package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.postgres.PostgresUsersRepository;

public class PostgresUserDetailsAssistant extends AbstractJwtUserDetailsService {

    public PostgresUserDetailsAssistant(
            PostgresUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
