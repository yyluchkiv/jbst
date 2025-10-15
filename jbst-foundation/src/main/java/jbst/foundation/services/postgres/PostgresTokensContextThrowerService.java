package jbst.foundation.services.postgres;

import jbst.foundation.assistants.userdetails.PostgresUserDetailsAssistant;
import jbst.foundation.repositories.postgres.PostgresJbstUsersSessionsRepository;
import jbst.foundation.services.abstracts.AbstractTokensContextThrowerService;
import jbst.foundation.utils.JbstSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresTokensContextThrowerService extends AbstractTokensContextThrowerService {

    @Autowired
    public PostgresTokensContextThrowerService(
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
