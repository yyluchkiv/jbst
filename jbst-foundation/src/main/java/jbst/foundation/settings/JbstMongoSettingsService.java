package jbst.foundation.settings;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.mongo.JbstMongoInvitation;
import jbst.foundation.domain.databases.mongo.JbstMongoUser;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.JbstPropertyUserOnInit;
import jbst.foundation.repositories.mongo.JbstMongoInvitationsRepository;
import jbst.foundation.repositories.mongo.JbstMongoSettingsRepository;
import jbst.foundation.repositories.mongo.JbstMongoUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class JbstMongoSettingsService extends JbstSettingsService {

    protected final JbstMongoInvitationsRepository invitationsRepository;
    protected final JbstMongoUsersRepository usersRepository;

    public JbstMongoSettingsService(
            JbstMongoSettingsRepository settingsRepository,
            JbstMongoInvitationsRepository invitationsRepository,
            JbstMongoUsersRepository usersRepository,
            JbstProperties jbstProperties
    ) {
        super(settingsRepository, invitationsRepository, usersRepository, jbstProperties);
        this.invitationsRepository = invitationsRepository;
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

    @Override
    public void initInvitations(JbstPropertyUserOnInit userOnInit, Set<SimpleGrantedAuthority> authorities) {
        var invitations = IntStream.range(0, 10)
                .mapToObj(i ->
                        new JbstMongoInvitation(
                                userOnInit.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.invitationsRepository.saveAll(invitations);
    }
}
