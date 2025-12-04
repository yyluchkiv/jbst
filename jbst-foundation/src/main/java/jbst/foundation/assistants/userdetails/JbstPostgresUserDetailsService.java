package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;

public class JbstPostgresUserDetailsService extends JbstUserDetailsService {

    public JbstPostgresUserDetailsService(JbstPostgresUsersRepository usersRepository) {
        super(usersRepository);
    }
}
