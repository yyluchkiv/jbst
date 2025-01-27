package jbst.iam.server.postgres.services.impl;

import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.domain.postgres.db.PostgresDbUser;
import jbst.iam.repositories.postgres.PostgresUsersRepository;
import jbst.iam.server.base.services.UsersService;
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
