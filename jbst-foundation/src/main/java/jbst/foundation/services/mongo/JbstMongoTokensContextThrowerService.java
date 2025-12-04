package jbst.foundation.services.mongo;

import jbst.foundation.assistants.userdetails.JbstMongoUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.repositories.mongo.JbstMongoUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractTokensContextThrowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JbstMongoTokensContextThrowerService extends JbstAbstractTokensContextThrowerService {

    @Autowired
    public JbstMongoTokensContextThrowerService(
            JbstMongoUserDetailsService userDetailsAssistant,
            JbstMongoUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
