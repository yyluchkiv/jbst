package jbst.foundation.settings;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbInvitation;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.UserOnInit;
import jbst.foundation.repositories.postgres.PostgresJbstInvitationsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstSettingsRepository;
import jbst.foundation.repositories.postgres.PostgresJbstUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

public class PostgresJbstSettingsService extends JbstSettingsService {

    private final PostgresJbstInvitationsRepository invitationsRepository;
    private final PostgresJbstUsersRepository usersRepository;

    public PostgresJbstSettingsService(
            PostgresJbstSettingsRepository settingsRepository,
            PostgresJbstInvitationsRepository invitationsRepository,
            PostgresJbstUsersRepository usersRepository,
            JbstProperties jbstProperties
    ) {
        super(settingsRepository, invitationsRepository, usersRepository, jbstProperties);
        this.invitationsRepository = invitationsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public long initUsers(List<UserOnInit> usersOnInit) {
        var users = usersOnInit.stream().
                map(userOnInit -> {
                    var username = userOnInit.getUsername();
                    var user = new PostgresDbUser(
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
                        new PostgresDbInvitation(
                                userOnInit.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.invitationsRepository.saveAll(invitations);
    }
}
