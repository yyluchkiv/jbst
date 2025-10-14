package jbst.server.iam.mongodb.services.impl;

import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.mongo.MongoUsersRepository;
import jbst.server.iam.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersServiceImpl implements UsersService {

    // Repositories
    private final MongoUsersRepository mongoUsersRepository;

    @Override
    public List<JwtUser> findAll() {
        return this.mongoUsersRepository.findAll().stream().map(MongoDbUser::asJwtUser).toList();
    }
}
