package jbst.iam.assistants.userdetails;

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
