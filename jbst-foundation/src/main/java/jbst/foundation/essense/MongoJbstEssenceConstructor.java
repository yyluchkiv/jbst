package jbst.foundation.essense;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.mongo.MongoDbInvitation;
import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.foundation.repositories.mongo.MongoJbstInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoJbstUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

// TODO [YYL] fixme
public class MongoJbstEssenceConstructor extends JbstEssenceConstructor {

    // Repositories
    protected final MongoJbstInvitationsRepository mongoJbstInvitationsRepository;
    protected final MongoJbstUsersRepository mongoUsersRepository;

    public MongoJbstEssenceConstructor(
            MongoJbstInvitationsRepository invitationsRepository,
            MongoJbstUsersRepository usersRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                usersRepository,
                jbstProperties
        );
        this.mongoJbstInvitationsRepository = invitationsRepository;
        this.mongoUsersRepository = usersRepository;
    }

    @Override
    public long saveDefaultUsers(List<DefaultUser> defaultUsers) {
        var dbUsers = defaultUsers.stream().
                map(defaultUser -> {
                    var username = defaultUser.getUsername();
                    var user = new MongoDbUser(
                            UserCreationOption.STANDARD,
                            username,
                            defaultUser.getPassword(),
                            defaultUser.getZoneId(),
                            getSimpleGrantedAuthorities(defaultUser.getAuthorities()),
                            null,
                            defaultUser.isPasswordChangeRequired(),
                            JbstUserEmailDetails.unnecessary()
                    );
                    user.setEmail(defaultUser.getEmailOrNull());
                    return user;
                })
                .toList();
        this.mongoUsersRepository.saveAll(dbUsers);
        return dbUsers.size();
    }

    @Override
    public void saveInvitations(DefaultUser defaultUser, Set<SimpleGrantedAuthority> authorities) {
        var invitations = IntStream.range(0, 10)
                .mapToObj(i ->
                        new MongoDbInvitation(
                                defaultUser.getUsername(),
                                authorities
                        )
                )
                .toList();
        this.mongoJbstInvitationsRepository.saveAll(invitations);
    }
}
