package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.mongo.MongoUsersRepository;

public class MongoUserDetailsAssistant extends JbstJwtUserDetailsService {

    public MongoUserDetailsAssistant(
            MongoUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
