package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUser;
import jbst.foundation.domain.databases.JbstUserEmailDetails;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JbstJwtUser;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.*;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.base.AbstractAuthority.*;
import static jbst.foundation.domain.random.JbstRandom.*;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;
import static org.springframework.util.StringUtils.capitalize;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = MongoDbUser.MONGO_TABLE_NAME)
public class MongoDbUser {
    public static final String MONGO_TABLE_NAME = "jbst_users";

    @Id
    private String id;
    private JbstUserCreationOption creationOption;
    private Username username;
    private Password password;
    @Schema(type = "string")
    private ZoneId zoneId;
    private Set<SimpleGrantedAuthority> authorities;
    private Email email;
    private String name;
    private boolean passwordChangeRequired;
    private boolean enabled;
    private JbstUserEmailDetails emailDetails;
    private Map<String, Object> attributes;

    public MongoDbUser(
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

    public MongoDbUser(
            @NotNull RequestUserRegistration0 requestUserRegistration0,
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

    public MongoDbUser(
            @NotNull RequestUserRegistration1 requestUserRegistration1,
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

    public MongoDbUser(JbstJwtUser user) {
        this.id = user.id().value();
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

    public static MongoDbUser random(String username, String authority) {
        return random(username, Set.of(authority));
    }

    public static MongoDbUser random(String username, Set<String> authorities) {
        var user = new MongoDbUser(
                JbstUserCreationOption.random(),
                Username.of(username),
                Password.random(),
                true,
                randomZoneId(),
                getSimpleGrantedAuthorities(authorities),
                Email.of(username + "@" + JbstConstants.Domains.HARDCODED),
                randomBoolean(),
                JbstUserEmailDetails.random()
        );
        user.setName(capitalize(randomString()) + " " + capitalize(randomString()));
        user.setAttributes(
                Map.of(
                        randomString(), randomString(),
                        randomString(), randomLong()
                )
        );
        return user;
    }

    public static MongoDbUser randomSuperadmin(String username) {
        return random(username, SUPERADMIN);
    }

    public static MongoDbUser randomAdmin(String username) {
        return random(username, "admin");
    }

    public static List<MongoDbUser> dummies1() {
        return List.of(
                MongoDbUser.randomSuperadmin("sa1"),
                MongoDbUser.randomSuperadmin("sa2"),
                MongoDbUser.randomAdmin("admin1"),
                MongoDbUser.random("user1", Set.of("user", INVITATIONS_WRITE)),
                MongoDbUser.random("user2", Set.of("user", INVITATIONS_READ)),
                MongoDbUser.random("sa3", Set.of(INVITATIONS_READ, SUPERADMIN, INVITATIONS_WRITE))
        );
    }

    @JsonIgnore
    @Transient
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
