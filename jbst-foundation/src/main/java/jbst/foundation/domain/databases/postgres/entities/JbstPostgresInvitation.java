package jbst.foundation.domain.databases.postgres.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.converters.JbstPostgresConverters;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.postgres.superclasses.JbstPostgresAbstractPersistable0;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getResponseInvitationsAuthoritiesAsField;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
// JPA
@Entity
@Table(name = JbstPostgresInvitation.PG_TABLE_NAME)
public class JbstPostgresInvitation extends JbstPostgresAbstractPersistable0 {
    public static final String PG_TABLE_NAME = "jbst_invitations";

    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column(nullable = false, updatable = false)
    private Username owner;

    @Convert(converter = JbstPostgresConverters.SimpleGrantedAuthoritiesSetConverter.class)
    @Column(length = 1024, nullable = false)
    private Set<SimpleGrantedAuthority> authorities;

    @Column(nullable = false)
    private String code;

    @Convert(converter = JbstPostgresConverters.UsernameConverter.class)
    @Column
    private Username invited;

    public JbstPostgresInvitation(Username owner, Set<SimpleGrantedAuthority> authorities) {
        this.owner = owner;
        this.authorities = authorities;
        this.code = randomStringLetterOrNumbersOnly(JbstInvitation.DEFAULT_INVITATION_CODE_LENGTH);
    }

    public JbstPostgresInvitation(JbstInvitation invitation) {
        this.id = nonNull(invitation.id()) ? invitation.id().value() : null;
        this.owner = invitation.owner();
        this.authorities = invitation.authorities();
        this.code = invitation.code();
        this.invited = invitation.invited();
    }

    public static JbstPostgresInvitation admin(String owner) {
        return new JbstPostgresInvitation(Username.of(owner), getSimpleGrantedAuthorities("admin"));
    }

    public static JbstPostgresInvitation admin(String owner, String value) {
        var invitation = admin(owner);
        invitation.setCode(value);
        return invitation;
    }

    public static JbstPostgresInvitation admin(String owner, String value, String invited) {
        var invitation = admin(owner, value);
        invitation.setInvited(Username.of(invited));
        return invitation;
    }

    public static List<JbstPostgresInvitation> dummies1() {
        var invitation1 = JbstPostgresInvitation.admin("user1");
        var invitation2 = JbstPostgresInvitation.admin("user1");
        var invitation3 = JbstPostgresInvitation.admin("user2");
        var invitation4 = JbstPostgresInvitation.admin("user2");
        var invitation5 = JbstPostgresInvitation.admin("user2");
        var invitation6 = JbstPostgresInvitation.admin("user3");

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

    public static List<JbstPostgresInvitation> dummies2() {
        var invitation1 = JbstPostgresInvitation.admin("owner22", "value22");
        var invitation2 = JbstPostgresInvitation.admin("owner22", "abc");
        var invitation3 = JbstPostgresInvitation.admin("owner22", "value44");
        var invitation4 = JbstPostgresInvitation.admin("owner11", "value222");
        var invitation5 = JbstPostgresInvitation.admin("owner11", "value111");
        var invitation6 = JbstPostgresInvitation.admin("owner33", "value123", "invited1");
        var invitation7 = JbstPostgresInvitation.admin("owner34", "value234", "invited2");
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
    public JbstInvitationId invitationId() {
        return new JbstInvitationId(this.id);
    }

    @JsonIgnore
    @Transient
    public JbstInvitation invitation() {
        return new JbstInvitation(
                this.invitationId(),
                this.owner,
                this.authorities,
                this.code,
                this.invited
        );
    }

    @JsonIgnore
    @Transient
    public JbstResponseInvitation responseInvitation() {
        return JbstResponseInvitation.of(
                this.invitationId(),
                this.owner,
                getResponseInvitationsAuthoritiesAsField(this.authorities),
                this.code,
                this.invited
        );
    }
}
