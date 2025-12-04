package jbst.server.iam.mongo.services;

import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import jbst.server.iam.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MongoUsersService implements UsersService {

    // Repositories
    private final MongoJbstUsersRepository mongoUsersRepository;

    @Override
    public List<JbstJwtUser> findAll() {
        return this.mongoUsersRepository.findAll().stream().map(MongoDbUser::asJwtUser).toList();
    }
}
