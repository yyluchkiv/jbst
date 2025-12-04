package jbst.foundation.services.postgres;

import jbst.foundation.assistants.userdetails.JbstPostgresUserDetailsService;
import jbst.foundation.assistants.utils.JbstSecurityUtils;
import jbst.foundation.repositories.postgres.JbstPostgresUsersSessionsRepository;
import jbst.foundation.services.abstracts.JbstAbstractTokensContextThrowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstPostgresTokensContextThrowerService extends JbstAbstractTokensContextThrowerService {

    @Autowired
    public JbstPostgresTokensContextThrowerService(
            JbstPostgresUserDetailsService userDetailsAssistant,
            JbstPostgresUsersSessionsRepository usersSessionsRepository,
            JbstSecurityUtils securityUtils
    ) {
        super(
                userDetailsAssistant,
                usersSessionsRepository,
                securityUtils
        );
    }
}
