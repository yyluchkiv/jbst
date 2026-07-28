package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUser;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.databases.postgres.superclasses.JbstPostgresAbstractPersistable0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JbstJwtUser;
import lombok.*;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.domain.random.JbstRandom.*;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
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
@Table(name = JbstPostgresUser.PG_TABLE_NAME)
public class JbstPostgresUser extends JbstPostgresAbstractPersistable0 {
    public static final String PG_TABLE_NAME = "jbst_users";

    @Convert(converter = JbstPostgresConverters.UserCreationOptionConverter.class)
    @Column(name = "creation_option", nullable = false, updatable = false)
    private JbstUserCreationOption creationOption;

    @Basic
    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(nullable = false, updatable = false)
    private Username username;

    @Convert(converter = JbstPostgresConverters.PasswordConverter.class)
    @Column(nullable = false)
    private Password password;

    @Schema(type = "string")
    @Convert(converter = JbstPostgresConverters.ZoneIdConverter.class)
    @Column(name = "zone_id", nullable = false)
    private ZoneId zoneId;

    @Convert(converter = JbstPostgresConverters.SimpleGrantedAuthoritiesSetConverter.class)
    @Column(length = 1024, nullable = false)
    private Set<SimpleGrantedAuthority> authorities;

    @Basic
    @Convert(converter = JbstPostgresConverters.EmailConverter.class)
    @Column
    private Email email;

    @Column
    private String name;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Type(JsonBinaryType.class)
    @Column(name = "email_details", nullable = false)
    private JbstUserEmailDetails emailDetails;

    @Convert(converter = JbstPostgresConverters.MapStringsObjectsConverter.class)
    @Column(length = 65535)
    private Map<String, Object> attributes;

    public JbstPostgresUser(
            @NotNull JbstUserCreationOption creationOption,
            @NotNull Username username,
            @NotNull Password password,
            boolean enabled,
            @NotNull ZoneId zoneId,
            @NotNull Set<SimpleGrantedAuthority> authorities,
            @Nullable Email email,
            boolean passwordChangeRequired,
            @NotNull JbstUserEmailDetails emailDetails
    ) {
        this.creationOption = creationOption;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.zoneId = zoneId;
        this.authorities = authorities;
        this.email = email;
        this.passwordChangeRequired = passwordChangeRequired;
        this.emailDetails = emailDetails;
        this.attributes = new HashMap<>();
    }

    public JbstPostgresUser(
            @NotNull JbstRequestUserRegistration0 requestUserRegistration0,
            @NotNull Password password
    ) {
        this(
                JbstUserCreationOption.STANDARD,
                requestUserRegistration0.username(),
                password,
                true,
                requestUserRegistration0.zoneId(),
                new HashSet<>(),
                requestUserRegistration0.email(),
                false,
                JbstUserEmailDetails.required()
        );
    }

    public JbstPostgresUser(
            @NotNull JbstRequestUserRegistration1 requestUserRegistration1,
            @NotNull Password password,
            @NotNull JbstInvitation invitation
    ) {
        this(
                JbstUserCreationOption.STANDARD,
                requestUserRegistration1.username(),
                password,
                true,
                requestUserRegistration1.zoneId(),
                invitation.authorities(),
                null,
                false,
                JbstUserEmailDetails.unnecessary()
        );
    }

    public JbstPostgresUser(JbstJwtUser user) {
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

    public static JbstPostgresUser random(String username, Set<String> authorities) {
        var user = new JbstPostgresUser(
                JbstUserCreationOption.random(),
                Username.of(username),
                Password.random(),
                true,
                randomZoneId(),
                getSimpleGrantedAuthorities(authorities),
                Email.of(username + "@" + JbstConstants.Domains.FIXED),
                randomBoolean(),
                JbstUserEmailDetails.random()
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

    public static JbstPostgresUser random(String username, String authority) {
        return random(username, Set.of(authority));
    }

    public static JbstPostgresUser randomSuperadmin(String username) {
        return random(username, SUPERADMIN);
    }

    public static JbstPostgresUser randomAdmin(String username) {
        return random(username, "admin");
    }

    public static List<JbstPostgresUser> dummies1() {
        return List.of(
                JbstPostgresUser.randomSuperadmin("sa1"),
                JbstPostgresUser.randomSuperadmin("sa2"),
                JbstPostgresUser.randomAdmin("admin1"),
                JbstPostgresUser.random("user1", Set.of("user", INVITATIONS_WRITE)),
                JbstPostgresUser.random("user2", Set.of("user", INVITATIONS_READ)),
                JbstPostgresUser.random("sa3", Set.of(INVITATIONS_READ, SUPERADMIN, INVITATIONS_WRITE))
        );
    }

    @SuppressWarnings("unused")
    @JsonIgnore
    public Map<String, Object> getNotNullAttributes() {
        return nonNull(this.attributes) ? this.attributes : new HashMap<>();
    }

    @JsonIgnore
    @Transient
    public JbstUserId userId() {
        return new JbstUserId(this.id);
    }

    @JsonIgnore
    @Transient
    public JbstJwtUser asJwtUser() {
        return new JbstJwtUser(
                this.userId(),
                this.creationOption,
                this.username,
                this.password,
                this.enabled,
                this.zoneId,
                this.authorities,
                this.email,
                this.name,
                this.passwordChangeRequired,
                this.emailDetails,
                this.attributes
        );
    }

    @JsonIgnore
    @Transient
    public JbstUser asJbstUser() {
        return new JbstUser(
                this.userId(),
                this.creationOption,
                this.username,
                this.enabled,
                this.zoneId,
                this.authorities,
                this.email,
                this.name
        );
    }
}
