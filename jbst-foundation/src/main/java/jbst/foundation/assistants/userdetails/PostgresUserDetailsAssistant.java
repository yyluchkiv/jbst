package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.postgres.PostgresUsersRepository;

public class PostgresUserDetailsAssistant extends JbstJwtUserDetailsService {

    public PostgresUserDetailsAssistant(
            PostgresUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
