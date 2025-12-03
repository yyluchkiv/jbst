package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.databases.mongo.MongoDbUser;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.SpringAuthorities.SUPERADMIN;
import static jbst.foundation.domain.strings.JbstMessages.entityNotFound;
import static jbst.foundation.domain.tuples.TuplePresence.present;

public interface MongoJbstUsersRepository extends MongoRepository<MongoDbUser, String>, JbstUsersRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JwtUser> isPresent(UserId userId) {
        return this.findById(userId.value())
                .map(entity -> present(entity.asJwtUser()))
                .orElseGet(TuplePresence::absent);
    }

    default JwtUser loadUserByUsername(Username username) throws UsernameNotFoundException {
        var user = this.findByUsername(username);
        if (nonNull(user)) {
            return user.asJwtUser();
        } else {
            throw new UsernameNotFoundException(entityNotFound("Username", username.value()));
        }
    }

    default JwtUser findByUsernameAsJwtUserOrNull(Username username) {
        var user = this.findByUsername(username);
        return nonNull(user) ? user.asJwtUser() : null;
    }

    default JwtUser findByEmailAsJwtUserOrNull(Email email) {
        var user = this.findByEmail(email);
        return nonNull(user) ? user.asJwtUser() : null;
    }

    default JbstUsers findUsers() {
        return new JbstUsers(this.findAll().stream().map(MongoDbUser::asJbstUser).collect(Collectors.toList()));
    }

    default JbstUsers findUsersExcept(Username username) {
        return new JbstUsers(this.findByUsernameNot(username).stream().map(MongoDbUser::asJbstUser).collect(Collectors.toList()));
    }

    default void confirmEmail(Email email) {
        var user = this.findByEmail(email);
        if (nonNull(user)) {
            user.setEmailDetails(JbstUserEmailDetails.confirmed());
            this.save(user);
        }
    }

    default void resetPassword(Email email, Password password) {
        var user = this.findByEmail(email);
        if (nonNull(user)) {
            user.setPassword(password);
            this.save(user);
        }
    }

    default void resetPassword(Username username, Password password) {
        var user = this.findByUsername(username);
        if (nonNull(user)) {
            user.setPassword(password);
            this.save(user);
        }
    }

    default void disable(Username username) {
        var user = this.findByUsername(username);
        if (nonNull(user)) {
            user.setEnabled(false);
            this.save(user);
        }
    }

    default UserId saveAs(JwtUser user) {
        var entity = this.save(new MongoDbUser(user));
        return entity.userId();
    }

    default UserId saveAs(RequestUserRegistration0 requestUserRegistration0, Password password) {
        var user = new MongoDbUser(
                requestUserRegistration0,
                password
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default UserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation) {
        var user = new MongoDbUser(
                requestUserRegistration1,
                password,
                invitation
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default JwtUser saveAsOrThrow(JbstUserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws JbstExceptions.UsernameAlreadyExist {
        var exist = this.existsByUsername(username);
        if (exist) {
            throw new JbstExceptions.UsernameAlreadyExist(username);
        } else {
            return this.save(
                    new MongoDbUser(
                            creationOption,
                            username,
                            password,
                            true,
                            zoneId,
                            new HashSet<>(),
                            email,
                            false,
                            JbstUserEmailDetails.unnecessary()
                    )
            ).asJwtUser();
        }
    }

    // ================================================================================================================
    // Spring Data
    // ================================================================================================================
    MongoDbUser findByEmail(Email email);
    boolean existsByEmail(Email email);
    MongoDbUser findByUsername(Username username);
    boolean existsByUsername(Username username);
    List<MongoDbUser> findByUsernameNot(Username username);
    List<MongoDbUser> findByUsernameIn(Set<Username> usernames);
    List<MongoDbUser> findByUsernameIn(List<Username> usernames);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Query(value = "{ 'authorities': ?0}")
    List<MongoDbUser> findByAuthority(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': ?0}", fields = "{ 'id': 0, 'username' : 1}")
    List<MongoDbUser> findByAuthorityProjectionUsernames(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}")
    List<MongoDbUser> findByAuthorityNotEqual(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}", fields = "{ 'id': 0, 'username' : 1}")
    List<MongoDbUser> findByAuthorityNotEqualProjectionUsernames(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': ?0}", delete = true)
    void deleteByAuthority(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}", delete = true)
    void deleteByAuthorityNotEqual(SimpleGrantedAuthority authority);

    default List<MongoDbUser> findByAuthoritySuperadmin() {
        return this.findByAuthority(SUPERADMIN);
    }

    default Set<Username> findSuperadminsUsernames() {
        return this.findByAuthorityProjectionUsernames(SUPERADMIN).stream().map(MongoDbUser::getUsername).collect(Collectors.toSet());
    }

    default List<MongoDbUser> findByAuthorityNotSuperadmin() {
        return this.findByAuthorityNotEqual(SUPERADMIN);
    }

    default Set<Username> findNotSuperadminsUsernames() {
        return this.findByAuthorityNotEqualProjectionUsernames(SUPERADMIN).stream().map(MongoDbUser::getUsername).collect(Collectors.toSet());
    }

    default void deleteByAuthoritySuperadmin() {
        this.deleteByAuthority(SUPERADMIN);
    }

    default void deleteByAuthorityNotSuperadmin() {
        this.deleteByAuthorityNotEqual(SUPERADMIN);
    }
}
