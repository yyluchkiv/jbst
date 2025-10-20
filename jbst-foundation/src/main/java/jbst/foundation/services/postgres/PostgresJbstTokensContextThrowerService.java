package jbst.foundation.services.postgres;

import jbst.foundation.assistants.userdetails.PostgresUserDetailsAssistant;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractJbstTokensContextThrowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresJbstTokensContextThrowerService extends AbstractJbstTokensContextThrowerService {

    @Autowired
    public PostgresJbstTokensContextThrowerService(
            PostgresUserDetailsAssistant userDetailsAssistant,
            PostgresJbstUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
