package jbst.server.iam.postgres.services.impl;

import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.repositories.postgres.PostgresUsersRepository;
import jbst.server.iam.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersServiceImpl implements UsersService {

    // Repositories
    private final PostgresUsersRepository postgresUsersRepository;

    @Override
    public List<JwtUser> findAll() {
        return this.postgresUsersRepository.findAll().stream().map(PostgresDbUser::asJwtUser).toList();
    }
}
