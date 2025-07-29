package jbst.iam.essence;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.iam.domain.db.UserEmailDetails;
import jbst.iam.domain.enums.UserCreationOption;
import jbst.iam.domain.postgres.db.PostgresDbInvitation;
import jbst.iam.domain.postgres.db.PostgresDbUser;
import jbst.iam.repositories.postgres.PostgresInvitationsRepository;
import jbst.iam.repositories.postgres.PostgresUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

public class PostgresBaseEssenceConstructor extends AbstractEssenceConstructor {

    // Repositories
    protected final PostgresInvitationsRepository postgresInvitationsRepository;
    protected final PostgresUsersRepository postgresUsersRepository;

    public PostgresBaseEssenceConstructor(
            PostgresInvitationsRepository invitationsRepository,
            PostgresUsersRepository usersRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                usersRepository,
                jbstProperties
        );
        this.postgresInvitationsRepository = invitationsRepository;
        this.postgresUsersRepository = usersRepository;
    }

    @Override
    public long saveDefaultUsers(List<DefaultUser> defaultUsers) {
        var dbUsers = defaultUsers.stream().
                map(defaultUser -> {
                    var username = defaultUser.getUsername();
                    var user = new PostgresDbUser(
                            username,
                            UserCreationOption.STANDARD,
                            defaultUser.getPassword(),
                            defaultUser.getZoneId(),
                            getSimpleGrantedAuthorities(defaultUser.getAuthorities()),
                            null,
                            defaultUser.isPasswordChangeRequired(),
                            UserEmailDetails.unnecessary()
                    );
                    user.setEmail(defaultUser.getEmailOrNull());
                    return user;
                })
                .toList();
        this.postgresUsersRepository.saveAll(dbUsers);
        return dbUsers.size();
    }

    @Override
    public void saveInvitations(DefaultUser defaultUser, Set<SimpleGrantedAuthority> authorities) {
        var invitations = IntStream.range(0, 10)
                .mapToObj(i ->
                        new PostgresDbInvitation(
                                defaultUser.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.postgresInvitationsRepository.saveAll(invitations);
    }
}
