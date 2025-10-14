package jbst.foundation.assistants.userdetails;

import jbst.foundation.repositories.mongo.MongoUsersRepository;

public class MongoUserDetailsAssistant extends AbstractJwtUserDetailsService {

    public MongoUserDetailsAssistant(
            MongoUsersRepository usersRepository
    ) {
        super(
                usersRepository
        );
    }
}
