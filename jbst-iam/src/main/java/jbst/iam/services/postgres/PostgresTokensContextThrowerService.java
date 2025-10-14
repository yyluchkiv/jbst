package jbst.iam.services.postgres;

import jbst.foundation.utils.JbstSecurityUtils;
import jbst.iam.assistants.userdetails.PostgresUserDetailsAssistant;
import jbst.iam.repositories.postgres.PostgresUsersSessionsRepository;
import jbst.iam.services.abstracts.AbstractTokensContextThrowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresTokensContextThrowerService extends AbstractTokensContextThrowerService {

    @Autowired
    public PostgresTokensContextThrowerService(
            PostgresUserDetailsAssistant userDetailsAssistant,
            PostgresUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
