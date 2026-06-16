package jbst.foundation.services.postgres;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.postgres.JbstPostgresUsersRepository;
import jbst.foundation.repositories.postgres.JbstPostgresUsersTokensRepository;
import jbst.foundation.services.abstracts.JbstAbstractUsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class JbstPostgresUsersService extends JbstAbstractUsersService {

    private final JbstPostgresUsersRepository usersRepository;

    @Autowired
    public JbstPostgresUsersService(
            JbstPostgresUsersTokensRepository usersTokensRepository,
            JbstPostgresUsersRepository usersRepository,
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
                    var user = new JbstPostgresUser(
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
