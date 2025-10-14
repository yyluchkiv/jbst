package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.exceptions.base.UsernameAlreadyExistException;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.databases.postgres.entities.PostgresDbUser;
import jbst.foundation.domain.databases.postgres.projections.PostgresDbUserProjection1;
import jbst.foundation.repositories.UsersRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.SpringAuthorities.SUPERADMIN;
import static jbst.foundation.domain.tuples.TuplePresence.present;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.entityNotFound;

@SuppressWarnings({"JpaQlInspection", "SqlNoDataSourceInspection"})
public interface PostgresUsersRepository extends JpaRepository<PostgresDbUser, String>, JpaSpecificationExecutor<PostgresDbUser>, UsersRepository {
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

    default UserId saveAs(JwtUser user) {
        var entity = this.save(new PostgresDbUser(user));
        return entity.userId();
    }

    default UserId saveAs(RequestUserRegistration0 requestUserRegistration0, Password password) {
        var user = new PostgresDbUser(
                requestUserRegistration0,
                password
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default UserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation) {
        var user = new PostgresDbUser(
                requestUserRegistration1,
                password,
                invitation
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default JwtUser saveAsOrThrow(UserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws UsernameAlreadyExistException {
        var exist = this.existsByUsername(username);
        if (exist) {
            throw new UsernameAlreadyExistException(username);
        } else {
            return this.save(
                    new PostgresDbUser(
                            creationOption,
                            username,
                            password,
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
    PostgresDbUser findByEmail(Email email);
    boolean existsByEmail(Email email);
    PostgresDbUser findByUsername(Username username);
    boolean existsByUsername(Username username);
    List<PostgresDbUser> findByUsernameIn(Set<Username> usernames);
    List<PostgresDbUser> findByUsernameIn(List<Username> usernames);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Query(value = "SELECT * FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    List<PostgresDbUser> findByAuthority(@Param("authority") String authority);

    @Query(value = "SELECT u.username FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    List<PostgresDbUserProjection1> findByAuthorityProjectionUsernames(@Param("authority") String authority);

    @Query(value = "SELECT * FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    List<PostgresDbUser> findByAuthorityNotEqual(@Param("authority") String authority);

    @Query(value = "SELECT * FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    List<PostgresDbUserProjection1> findByAuthorityNotEqualProjectionUsernames(@Param("authority") String authority);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    void deleteByAuthority(@Param("authority") String authority);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM " + PostgresDbUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    void deleteByAuthorityNotEqual(@Param("authority") String authority);

    default List<PostgresDbUser> findByAuthoritySuperadmin() {
        return this.findByAuthority(SUPERADMIN.getAuthority());
    }

    default Set<Username> findSuperadminsUsernames() {
        return this.findByAuthorityProjectionUsernames(SUPERADMIN.getAuthority()).stream().map(PostgresDbUserProjection1::getAsUsername).collect(Collectors.toSet());
    }

    default List<PostgresDbUser> findByAuthorityNotSuperadmin() {
        return this.findByAuthorityNotEqual(SUPERADMIN.getAuthority());
    }

    default Set<Username> findNotSuperadminsUsernames() {
        return this.findByAuthorityNotEqualProjectionUsernames(SUPERADMIN.getAuthority()).stream().map(PostgresDbUserProjection1::getAsUsername).collect(Collectors.toSet());
    }

    @Transactional
    default void deleteByAuthoritySuperadmin() {
        this.deleteByAuthority(SUPERADMIN.getAuthority());
    }

    @Transactional
    default void deleteByAuthorityNotSuperadmin() {
        this.deleteByAuthorityNotEqual(SUPERADMIN.getAuthority());
    }
}
