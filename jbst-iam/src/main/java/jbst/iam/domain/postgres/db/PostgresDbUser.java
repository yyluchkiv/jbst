package jbst.iam.domain.postgres.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.converters.PostgresConverters;
import jbst.iam.converters.postgres.PostgresSetOfSimpleGrantedAuthoritiesConverter;
import jbst.iam.converters.postgres.PostgresUserCreationOptionConverter;
import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.db.UserEmailDetails;
import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.iam.domain.identifiers.UserId;
import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.domain.postgres.superclasses.PostgresDbAbstractPersistable0;
import lombok.*;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.utilities.random.RandomUtility.*;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static org.springframework.util.StringUtils.capitalize;

@SuppressWarnings("JpaDataSourceORMInspection")
// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = PostgresDbUser.PG_TABLE_NAME)
public class PostgresDbUser extends PostgresDbAbstractPersistable0 {
    public static final String PG_TABLE_NAME = "jbst_users";

    @Convert(converter = PostgresUserCreationOptionConverter.class)
    @Column(name = "creation_option", nullable = false, updatable = false)
    private UserCreationOption creationOption;

    @Basic
    @Convert(converter = PostgresConverters.UsernameConverter.class)
    @Column(nullable = false, updatable = false)
    private Username username;

    @Convert(converter = PostgresConverters.PasswordConverter.class)
    @Column(nullable = false)
    private Password password;

    @Schema(type = "string")
    @Convert(converter = PostgresConverters.ZoneIdConverter.class)
    @Column(name = "zone_id", nullable = false)
    private ZoneId zoneId;

    @Convert(converter = PostgresSetOfSimpleGrantedAuthoritiesConverter.class)
    @Column(length = 1024, nullable = false)
    private Set<SimpleGrantedAuthority> authorities;

    @Basic
    @Convert(converter = PostgresConverters.EmailConverter.class)
    @Column
    private Email email;

    @Column
    private String name;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired;

    @Type(JsonBinaryType.class)
    @Column(name = "email_details", nullable = false)
    private UserEmailDetails emailDetails;

    @Convert(converter = PostgresConverters.MapStringsObjectsConverter.class)
    @Column(length = 65535)
    private Map<String, Object> attributes;

    public PostgresDbUser(
            @NotNull UserCreationOption creationOption,
            @NotNull Username username,
            @NotNull Password password,
            @NotNull ZoneId zoneId,
            @NotNull Set<SimpleGrantedAuthority> authorities,
            @Nullable Email email,
            boolean passwordChangeRequired,
            @NotNull UserEmailDetails emailDetails
    ) {
        this.creationOption = creationOption;
        this.username = username;
        this.password = password;
        this.zoneId = zoneId;
        this.authorities = authorities;
        this.email = email;
        this.passwordChangeRequired = passwordChangeRequired;
        this.emailDetails = emailDetails;
        this.attributes = new HashMap<>();
    }

    public PostgresDbUser(
            @NotNull RequestUserRegistration0 requestUserRegistration0,
            @NotNull Password password
    ) {
        this(
                UserCreationOption.STANDARD,
                requestUserRegistration0.username(),
                password,
                requestUserRegistration0.zoneId(),
                new HashSet<>(),
                requestUserRegistration0.email(),
                false,
                UserEmailDetails.required()
        );
    }

    public PostgresDbUser(
            @NotNull RequestUserRegistration1 requestUserRegistration1,
            @NotNull Password password,
            @NotNull Invitation invitation
    ) {
        this(
                UserCreationOption.STANDARD,
                requestUserRegistration1.username(),
                password,
                requestUserRegistration1.zoneId(),
                invitation.authorities(),
                null,
                false,
                UserEmailDetails.unnecessary()
        );
    }

    public PostgresDbUser(JwtUser user) {
        this.id = nonNull(user.id()) ? user.id().value() : null;
        this.creationOption = user.creationOption();
        this.username = user.username();
        this.password = user.password();
        this.zoneId = user.zoneId();
        this.authorities = user.authorities();
        this.email = user.email();
        this.name = user.name();
        this.passwordChangeRequired = user.passwordChangeRequired();
        this.emailDetails = user.emailDetails();
        this.attributes = user.attributes();
    }

    public static PostgresDbUser random(String username, Set<String> authorities) {
        var user = new PostgresDbUser(
                UserCreationOption.random(),
                Username.of(username),
                Password.random(),
                randomZoneId(),
                getSimpleGrantedAuthorities(authorities),
                Email.of(username + "@" + JbstConstants.Domains.HARDCODED),
                randomBoolean(),
                UserEmailDetails.random()
        );
        user.setName(capitalize(username) + " " + capitalize(username));
        user.setAttributes(
                Map.of(
                        randomString(), randomString(),
                        randomString(), randomLong()
                )
        );
        return user;
    }

    public static PostgresDbUser random(String username, String authority) {
        return random(username, Set.of(authority));
    }

    public static PostgresDbUser randomSuperadmin(String username) {
        return random(username, SUPERADMIN);
    }

    public static PostgresDbUser randomAdmin(String username) {
        return random(username, "admin");
    }

    public static List<PostgresDbUser> dummies1() {
        return List.of(
                PostgresDbUser.randomSuperadmin("sa1"),
                PostgresDbUser.randomSuperadmin("sa2"),
                PostgresDbUser.randomAdmin("admin1"),
                PostgresDbUser.random("user1", Set.of("user", INVITATIONS_WRITE)),
                PostgresDbUser.random("user2", Set.of("user", INVITATIONS_READ)),
                PostgresDbUser.random("sa3", Set.of(INVITATIONS_READ, SUPERADMIN, INVITATIONS_WRITE))
        );
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public Map<String, Object> getNotNullAttributes() {
        return nonNull(this.attributes) ? this.attributes : new HashMap<>();
    }

    @JsonIgnore
    @Transient
    public UserId userId() {
        return new UserId(this.id);
    }

    @JsonIgnore
    @Transient
    public JwtUser asJwtUser() {
        return new JwtUser(
                this.userId(),
                this.creationOption,
                this.username,
                this.password,
                this.zoneId,
                this.authorities,
                this.email,
                this.name,
                this.passwordChangeRequired,
                this.emailDetails,
                this.attributes
        );
    }
}
