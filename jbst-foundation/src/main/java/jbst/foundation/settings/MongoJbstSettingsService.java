package jbst.foundation.settings;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.mongo.MongoDbInvitation;
import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.properties.base.UserOnInit;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstSettingsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

public class MongoJbstSettingsService extends JbstSettingsService {

    private final MongoJbstInvitationsRepository invitationsRepository;
    private final MongoJbstUsersRepository usersRepository;

    public MongoJbstSettingsService(
            MongoJbstSettingsRepository settingsRepository,
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository
    ) {
        super(settingsRepository, invitationsRepository, usersRepository);
        this.invitationsRepository = invitationsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public long initUsers(List<UserOnInit> usersOnInit) {
        var users = usersOnInit.stream().
                map(userOnInit -> {
                    var username = userOnInit.getUsername();
                    var user = new MongoDbUser(
                            UserCreationOption.STANDARD,
                            username,
                            userOnInit.getPassword(),
                            userOnInit.getZoneId(),
                            getSimpleGrantedAuthorities(userOnInit.getAuthorities()),
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

    @Override
    public void initInvitations(UserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities) {
        var invitations = IntStream.range(0, 10)
                .mapToObj(i ->
                        new MongoDbInvitation(
                                userOnInit.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.invitationsRepository.saveAll(invitations);
    }
}
