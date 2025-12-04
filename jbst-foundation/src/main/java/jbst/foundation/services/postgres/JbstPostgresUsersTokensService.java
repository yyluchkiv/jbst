package jbst.foundation.services.postgres;

import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JbstPostgresUsersTokensService extends JbstAbstractUsersTokensService {

    @Autowired
    public JbstPostgresUsersTokensService(
            JbstPostgresUsersTokensRepository usersTokensRepository,
            JbstPostgresUsersRepository usersRepository
    ) {
        super(
                usersTokensRepository,
                usersRepository
        );
    }
}
