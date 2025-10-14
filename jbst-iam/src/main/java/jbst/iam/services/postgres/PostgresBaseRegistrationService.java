package jbst.iam.services.postgres;

import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.repositories.postgres.PostgresInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresUsersRepository;
import jbst.iam.services.abstracts.AbstractBaseRegistrationService;
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
            PostgresInvitationsRepository invitationsRepository,
            PostgresUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                invitationsRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
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
