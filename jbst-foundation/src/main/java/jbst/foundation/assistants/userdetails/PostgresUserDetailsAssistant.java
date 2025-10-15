package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;

public class PostgresUserDetailsAssistant extends JbstJwtUserDetailsService {

    public PostgresUserDetailsAssistant(
            PostgresJbstUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
