package jbst.iam.domain.postgres.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.iam.converters.postgres.PostgresSetOfSimpleGrantedAuthoritiesConverter;
import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.responses.ResponseInvitation;
import jbst.iam.domain.identifiers.InvitationId;
import jbst.iam.domain.postgres.superclasses.PostgresDbAbstractPersistable0;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.postgres.PostgresUsernameConverter;

import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getResponseInvitationsAuthoritiesAsField;
import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = PostgresDbInvitation.PG_TABLE_NAME)
public class PostgresDbInvitation extends PostgresDbAbstractPersistable0 {
    public static final String PG_TABLE_NAME = "jbst_invitations";

    @Convert(converter = PostgresUsernameConverter.class)
    @Column(nullable = false, updatable = false)
    private Username owner;

    @Convert(converter = PostgresSetOfSimpleGrantedAuthoritiesConverter.class)
    @Column(length = 1024, nullable = false)
    private Set<SimpleGrantedAuthority> authorities;

    @Column(nullable = false)
    private String code;

    @Convert(converter = PostgresUsernameConverter.class)
    @Column
    private Username invited;

    public PostgresDbInvitation(Username owner, Set<SimpleGrantedAuthority> authorities) {
        this.owner = owner;
        this.authorities = authorities;
        this.code = randomStringLetterOrNumbersOnly(Invitation.DEFAULT_INVITATION_CODE_LENGTH);
    }

    public PostgresDbInvitation(Invitation invitation) {
        this.id = nonNull(invitation.id()) ? invitation.id().value() : null;
        this.owner = invitation.owner();
        this.authorities = invitation.authorities();
        this.code = invitation.code();
        this.invited = invitation.invited();
    }

    public static PostgresDbInvitation admin(String owner) {
        return new PostgresDbInvitation(Username.of(owner), getSimpleGrantedAuthorities("admin"));
    }

    public static PostgresDbInvitation admin(String owner, String value) {
        var invitation = admin(owner);
        invitation.setCode(value);
        return invitation;
    }

    public static PostgresDbInvitation admin(String owner, String value, String invited) {
        var invitation = admin(owner, value);
        invitation.setInvited(Username.of(invited));
        return invitation;
    }

    public static List<PostgresDbInvitation> dummies1() {
        var invitation1 = PostgresDbInvitation.admin("user1");
        var invitation2 = PostgresDbInvitation.admin("user1");
        var invitation3 = PostgresDbInvitation.admin("user2");
        var invitation4 = PostgresDbInvitation.admin("user2");
        var invitation5 = PostgresDbInvitation.admin("user2");
        var invitation6 = PostgresDbInvitation.admin("user3");

        invitation4.setInvited(Username.of("superadmin"));

        return List.of(
                invitation1,
                invitation2,
                invitation3,
                invitation4,
                invitation5,
                invitation6
        );
    }

    public static List<PostgresDbInvitation> dummies2() {
        var invitation1 = PostgresDbInvitation.admin("owner22", "value22");
        var invitation2 = PostgresDbInvitation.admin("owner22", "abc");
        var invitation3 = PostgresDbInvitation.admin("owner22", "value44");
        var invitation4 = PostgresDbInvitation.admin("owner11", "value222");
        var invitation5 = PostgresDbInvitation.admin("owner11", "value111");
        var invitation6 = PostgresDbInvitation.admin("owner33", "value123", "invited1");
        var invitation7 = PostgresDbInvitation.admin("owner34", "value234", "invited2");
        return List.of(
                invitation1,
                invitation2,
                invitation3,
                invitation4,
                invitation5,
                invitation6,
                invitation7
        );
    }

    @JsonIgnore
    @Transient
    public InvitationId invitationId() {
        return new InvitationId(this.id);
    }

    @JsonIgnore
    @Transient
    public Invitation invitation() {
        return new Invitation(
                this.invitationId(),
                this.owner,
                this.authorities,
                this.code,
                this.invited
        );
    }

    @JsonIgnore
    @Transient
    public ResponseInvitation responseInvitation() {
        return ResponseInvitation.of(
                this.invitationId(),
                this.owner,
                getResponseInvitationsAuthoritiesAsField(this.authorities),
                this.code,
                this.invited
        );
    }
}
