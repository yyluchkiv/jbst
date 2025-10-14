package jbst.iam.services.mongodb;

import jbst.foundation.utils.JbstSecurityUtils;
import jbst.iam.assistants.userdetails.MongoUserDetailsAssistant;
import jbst.iam.repositories.mongodb.MongoUsersSessionsRepository;
import jbst.iam.services.abstracts.AbstractTokensContextThrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoTokensContextThrowerService extends AbstractTokensContextThrowerService {

    @Autowired
    public MongoTokensContextThrowerService(
            MongoUserDetailsAssistant userDetailsAssistant,
            MongoUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
