package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;

public class JbstMongoUserDetailsService extends JbstUserDetailsService {

    public JbstMongoUserDetailsService(JbstMongoUsersRepository usersRepository) {
        super(usersRepository);
    }
}
