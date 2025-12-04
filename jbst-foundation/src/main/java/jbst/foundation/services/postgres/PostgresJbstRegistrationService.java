package jbst.foundation.services.postgres;

import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistrationMagicLink;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.services.base.JbstUsersEmailsService;
import jbst.foundation.services.abstracts.AbstractJbstRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PostgresJbstRegistrationService extends AbstractJbstRegistrationService {

    @Autowired
    public PostgresJbstRegistrationService(
            JbstUsersEmailsService usersEmailsService,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersRepository usersRepository,
            PostgresJbstUsersTokensRepository usersTokensRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersEmailsService,
                invitationsRepository,
                usersRepository,
                usersTokensRepository,
                bCryptPasswordEncoder
        );
    }

    @Transactional
    @Override
    public void registerMagicLink(JbstRequestUserRegistrationMagicLink request) {
        super.registerMagicLink(request);
    }

    @Transactional
    @Override
    public void register0(JbstRequestUserRegistration0 request) {
        super.register0(request);
    }

    @Transactional
    @Override
    public void register1(JbstRequestUserRegistration1 request) {
        super.register1(request);
    }
}
