package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JbstPostgresUsersService extends JbstAbstractUsersService {

    @Autowired
    public JbstPostgresUsersService(
            JbstPostgresUsersTokensRepository usersTokensRepository,
            JbstPostgresUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder
        );
    }
}
