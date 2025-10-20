package jbst.foundation.services.mongodb;

import jbst.foundation.assistants.userdetails.MongoUserDetailsAssistant;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.repositories.mongo.MongoJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstTokensContextThrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MongoJbstTokensContextThrowerService extends AbstractJbstTokensContextThrowerService {

    @Autowired
    public MongoJbstTokensContextThrowerService(
            MongoUserDetailsAssistant userDetailsAssistant,
            MongoJbstUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
