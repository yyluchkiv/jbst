package jbst.iam.essence;

import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.properties.base.DefaultUser;
import jbst.iam.domain.db.UserEmailDetails;
import jbst.iam.domain.enums.UserCreationOption;
import jbst.iam.domain.mongodb.MongoDbInvitation;
import jbst.iam.domain.mongodb.MongoDbUser;
import jbst.iam.repositories.mongodb.MongoInvitationsRepository;
import jbst.iam.repositories.mongodb.MongoUsersRepository;
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
                            UserEmailDetails.unnecessary()
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
