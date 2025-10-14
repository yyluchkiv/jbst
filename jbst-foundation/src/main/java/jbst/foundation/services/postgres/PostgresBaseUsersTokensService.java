package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.PostgresUsersRepository;
import jbst.foundation.repositories.postgres.PostgresUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractBaseUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresBaseUsersTokensService extends AbstractBaseUsersTokensService {

    @Autowired
    public PostgresBaseUsersTokensService(
            PostgresUsersTokensRepository usersTokensRepository,
            PostgresUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
