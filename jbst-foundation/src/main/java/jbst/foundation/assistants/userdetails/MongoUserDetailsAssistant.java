package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;

public class MongoUserDetailsAssistant extends JbstJwtUserDetailsService {

    public MongoUserDetailsAssistant(
            MongoJbstUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
