package jbst.foundation.repositories.mongo;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.databases.mongo.JbstMongoUser;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JbstJwtUser;
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

public interface JbstMongoUsersRepository extends MongoRepository<JbstMongoUser, String>, JbstUsersRepository {
    // ================================================================================================================
    // Any
    // ================================================================================================================
    default TuplePresence<JbstJwtUser> isPresent(JbstUserId userId) {
        return this.findById(userId.value())
                .map(entity -> present(entity.asJwtUser()))
                .orElseGet(TuplePresence::absent);
    }

    default JbstJwtUser loadUserByUsername(Username username) throws UsernameNotFoundException {
        var user = this.findByUsername(username);
        if (nonNull(user)) {
            return user.asJwtUser();
        } else {
            throw new UsernameNotFoundException(entityNotFound("Username", username.value()));
        }
    }

    default JbstJwtUser findByUsernameAsJwtUserOrNull(Username username) {
        var user = this.findByUsername(username);
        return nonNull(user) ? user.asJwtUser() : null;
    }

    default JbstJwtUser findByEmailAsJwtUserOrNull(Email email) {
        var user = this.findByEmail(email);
        return nonNull(user) ? user.asJwtUser() : null;
    }

    default JbstUsers findUsers() {
        return new JbstUsers(this.findAll().stream().map(JbstMongoUser::asJbstUser).collect(Collectors.toList()));
    }

    default JbstUsers findUsersExcept(Username username) {
        return new JbstUsers(this.findByUsernameNot(username).stream().map(JbstMongoUser::asJbstUser).collect(Collectors.toList()));
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

    default JbstUserId saveAs(JbstJwtUser user) {
        var entity = this.save(new JbstMongoUser(user));
        return entity.userId();
    }

    default JbstUserId saveAs(JbstRequestUserRegistration0 requestUserRegistration0, Password password) {
        var user = new JbstMongoUser(
                requestUserRegistration0,
                password
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default JbstUserId saveAs(JbstRequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation) {
        var user = new JbstMongoUser(
                requestUserRegistration1,
                password,
                invitation
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default JbstJwtUser saveAsOrThrow(JbstUserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws JbstExceptions.UsernameAlreadyExist {
        var exist = this.existsByUsername(username);
        if (exist) {
            throw new JbstExceptions.UsernameAlreadyExist(username);
        } else {
            return this.save(
                    new JbstMongoUser(
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
    JbstMongoUser findByEmail(Email email);
    boolean existsByEmail(Email email);
    JbstMongoUser findByUsername(Username username);
    boolean existsByUsername(Username username);
    List<JbstMongoUser> findByUsernameNot(Username username);
    List<JbstMongoUser> findByUsernameIn(Set<Username> usernames);
    List<JbstMongoUser> findByUsernameIn(List<Username> usernames);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Query(value = "{ 'authorities': ?0}")
    List<JbstMongoUser> findByAuthority(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': ?0}", fields = "{ 'id': 0, 'username' : 1}")
    List<JbstMongoUser> findByAuthorityProjectionUsernames(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}")
    List<JbstMongoUser> findByAuthorityNotEqual(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}", fields = "{ 'id': 0, 'username' : 1}")
    List<JbstMongoUser> findByAuthorityNotEqualProjectionUsernames(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': ?0}", delete = true)
    void deleteByAuthority(SimpleGrantedAuthority authority);

    @Query(value = "{ 'authorities': { '$ne': ?0}}", delete = true)
    void deleteByAuthorityNotEqual(SimpleGrantedAuthority authority);

    default List<JbstMongoUser> findByAuthoritySuperadmin() {
        return this.findByAuthority(SUPERADMIN);
    }

    default Set<Username> findSuperadminsUsernames() {
        return this.findByAuthorityProjectionUsernames(SUPERADMIN).stream().map(JbstMongoUser::getUsername).collect(Collectors.toSet());
    }

    default List<JbstMongoUser> findByAuthorityNotSuperadmin() {
        return this.findByAuthorityNotEqual(SUPERADMIN);
    }

    default Set<Username> findNotSuperadminsUsernames() {
        return this.findByAuthorityNotEqualProjectionUsernames(SUPERADMIN).stream().map(JbstMongoUser::getUsername).collect(Collectors.toSet());
    }

    default void deleteByAuthoritySuperadmin() {
        this.deleteByAuthority(SUPERADMIN);
    }

    default void deleteByAuthorityNotSuperadmin() {
        this.deleteByAuthorityNotEqual(SUPERADMIN);
    }
}
