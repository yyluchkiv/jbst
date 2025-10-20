package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersTokensRepository;
import jbst.foundation.services.abstracts.AbstractJbstUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostgresJbstUsersTokensService extends AbstractJbstUsersTokensService {

    @Autowired
    public PostgresJbstUsersTokensService(
            PostgresJbstUsersTokensRepository usersTokensRepository,
            PostgresJbstUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
