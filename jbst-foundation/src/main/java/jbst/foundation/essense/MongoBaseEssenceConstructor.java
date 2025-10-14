package jbst.foundation.essense;

import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.mongo.MongoDbInvitation;
import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.foundation.repositories.mongo.MongoInvitationsRepository;
import jbst.foundation.repositories.mongo.MongoUsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

public class MongoBaseEssenceConstructor extends AbstractEssenceConstructor {

    // Repositories
    protected final MongoInvitationsRepository mongoInvitationsRepository;
    protected final MongoUsersRepository mongoUsersRepository;

    public MongoBaseEssenceConstructor(
            MongoInvitationsRepository invitationsRepository,
            MongoUsersRepository usersRepository,
            JbstProperties jbstProperties
    ) {
        super(
                invitationsRepository,
                usersRepository,
                jbstProperties
        );
        this.mongoInvitationsRepository = invitationsRepository;
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
        this.mongoInvitationsRepository.saveAll(invitations);
    }
}
