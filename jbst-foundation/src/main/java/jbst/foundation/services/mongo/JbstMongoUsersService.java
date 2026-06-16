package jbst.foundation.services.mongo;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.mongo.JbstMongoUser;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JbstMongoUsersService extends JbstAbstractUsersService {

    private final JbstMongoUsersRepository usersRepository;

    @Autowired
    public JbstMongoUsersService(
            JbstMongoUsersTokensRepository usersTokensRepository,
            JbstMongoUsersRepository usersRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JbstProperties jbstProperties
    ) {
        super(
                usersTokensRepository,
                usersRepository,
                bCryptPasswordEncoder,
                jbstProperties
        );
        this.usersRepository = usersRepository;
    }

    @Override
    public long initUsers(List<JbstPropertyUserOnInit> usersOnInit) {
        var users = usersOnInit.stream().
                map(userOnInit -> {
                    var username = userOnInit.getUsername();
                    var user = new JbstMongoUser(
                            JbstUserCreationOption.STANDARD,
                            username,
                            userOnInit.getPassword(),
                            true,
                            userOnInit.getZoneId(),
                            userOnInit.getSimpleGrantedAuthorities(),
                            null,
                            userOnInit.isPasswordChangeRequired(),
                            JbstUserEmailDetails.unnecessary()
                    );
                    user.setEmail(userOnInit.getEmailOrNull());
                    return user;
                })
                .toList();
        this.usersRepository.saveAll(users);
        return users.size();
    }
}
