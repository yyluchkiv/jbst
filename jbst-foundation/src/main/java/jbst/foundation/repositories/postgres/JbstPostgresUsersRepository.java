package jbst.foundation.repositories.postgres;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.databases.postgres.entities.JbstPostgresUser;
import jbst.foundation.domain.databases.postgres.projections.JbstPostgresUserProjection1;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.foundation.repositories.JbstUsersRepository;
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
import static jbst.foundation.domain.strings.JbstMessages.entityNotFound;
import static jbst.foundation.domain.tuples.TuplePresence.present;

@SuppressWarnings({"JpaQlInspection", "SqlNoDataSourceInspection"})
public interface JbstPostgresUsersRepository extends JpaRepository<JbstPostgresUser, String>, JpaSpecificationExecutor<JbstPostgresUser>, JbstUsersRepository {
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
        return new JbstUsers(this.findAll().stream().map(JbstPostgresUser::asJbstUser).collect(Collectors.toList()));
    }

    default JbstUsers findUsersExcept(Username username) {
        return new JbstUsers(this.findByUsernameNot(username).stream().map(JbstPostgresUser::asJbstUser).collect(Collectors.toList()));
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
        var entity = this.save(new JbstPostgresUser(user));
        return entity.userId();
    }

    default JbstUserId saveAs(JbstRequestUserRegistration0 requestUserRegistration0, Password password) {
        var user = new JbstPostgresUser(
                requestUserRegistration0,
                password
        );
        var entity = this.save(user);
        return entity.userId();
    }

    default JbstUserId saveAs(JbstRequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation) {
        var user = new JbstPostgresUser(
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
                    new JbstPostgresUser(
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
    JbstPostgresUser findByEmail(Email email);
    boolean existsByEmail(Email email);
    JbstPostgresUser findByUsername(Username username);
    boolean existsByUsername(Username username);
    List<JbstPostgresUser> findByUsernameNot(Username username);
    List<JbstPostgresUser> findByUsernameIn(Set<Username> usernames);
    List<JbstPostgresUser> findByUsernameIn(List<Username> usernames);

    // ================================================================================================================
    // Queries
    // ================================================================================================================
    @Query(value = "SELECT * FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    List<JbstPostgresUser> findByAuthority(@Param("authority") String authority);

    @Query(value = "SELECT u.username FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    List<JbstPostgresUserProjection1> findByAuthorityProjectionUsernames(@Param("authority") String authority);

    @Query(value = "SELECT * FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    List<JbstPostgresUser> findByAuthorityNotEqual(@Param("authority") String authority);

    @Query(value = "SELECT * FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    List<JbstPostgresUserProjection1> findByAuthorityNotEqualProjectionUsernames(@Param("authority") String authority);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities LIKE %:authority%", nativeQuery = true)
    void deleteByAuthority(@Param("authority") String authority);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM " + JbstPostgresUser.PG_TABLE_NAME + " u WHERE u.authorities NOT LIKE %:authority%", nativeQuery = true)
    void deleteByAuthorityNotEqual(@Param("authority") String authority);

    default List<JbstPostgresUser> findByAuthoritySuperadmin() {
        return this.findByAuthority(SUPERADMIN.getAuthority());
    }

    default Set<Username> findSuperadminsUsernames() {
        return this.findByAuthorityProjectionUsernames(SUPERADMIN.getAuthority()).stream().map(JbstPostgresUserProjection1::getAsUsername).collect(Collectors.toSet());
    }

    default List<JbstPostgresUser> findByAuthorityNotSuperadmin() {
        return this.findByAuthorityNotEqual(SUPERADMIN.getAuthority());
    }

    default Set<Username> findNotSuperadminsUsernames() {
        return this.findByAuthorityNotEqualProjectionUsernames(SUPERADMIN.getAuthority()).stream().map(JbstPostgresUserProjection1::getAsUsername).collect(Collectors.toSet());
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
