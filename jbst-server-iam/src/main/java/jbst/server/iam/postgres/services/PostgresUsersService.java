package jbst.server.iam.postgres.services;

import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import jbst.server.iam.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostgresUsersService implements UsersService {

    // Repositories
    private final PostgresJbstUsersRepository postgresUsersRepository;

    @Override
    public List<JwtUser> findAll() {
        return this.postgresUsersRepository.findAll().stream().map(PostgresDbUser::asJwtUser).toList();
    }
}
