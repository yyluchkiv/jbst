package jbst.server.iam.postgres.services;

import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.server.iam.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostgresUsersService implements UsersService {

    // Repositories
    private final JbstPostgresUsersRepository postgresUsersRepository;

    @Override
    public List<JbstJwtUser> findAll() {
        return this.postgresUsersRepository.findAll().stream().map(JbstPostgresUser::asJwtUser).toList();
    }
}
