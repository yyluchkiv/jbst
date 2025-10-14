package jbst.iam.services.postgres;

import jbst.foundation.repositories.postgres.PostgresUsersRepository;
import jbst.foundation.repositories.postgres.PostgresUsersTokensRepository;
import jbst.iam.services.abstracts.AbstractBaseUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostgresBaseUsersService extends AbstractBaseUsersService {

    @Autowired
    public PostgresBaseUsersService(
            PostgresUsersTokensRepository usersTokensRepository,
            PostgresUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
