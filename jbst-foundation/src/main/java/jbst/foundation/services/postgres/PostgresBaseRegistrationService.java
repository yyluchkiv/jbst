package jbst.foundation.services.postgres;

import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.services.base.UsersEmailsService;
import jbst.foundation.services.abstracts.AbstractBaseRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PostgresBaseRegistrationService extends AbstractBaseRegistrationService {

    @Autowired
    public PostgresBaseRegistrationService(
            UsersEmailsService usersEmailsService,
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
    public void registerMagicLink(RequestUserRegistrationMagicLink request) {
        super.registerMagicLink(request);
    }

    @Transactional
    @Override
    public void register0(RequestUserRegistration0 request) {
        super.register0(request);
    }

    @Transactional
    @Override
    public void register1(RequestUserRegistration1 request) {
        super.register1(request);
    }
}
