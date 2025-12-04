package jbst.foundation.domain.databases.mongo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.dto.responses.JbstResponseInvitation;
import jbst.foundation.domain.ids.JbstInvitationId;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getResponseInvitationsAuthoritiesAsField;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

// Lombok
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
// Mongodb
@Document(collection = JbstMongoInvitation.MONGO_TABLE_NAME)
public class JbstMongoInvitation {
    public static final String MONGO_TABLE_NAME = "jbst_invitations";

    @Id
    private String id;
    private Username owner;
    private Set<SimpleGrantedAuthority> authorities;
    private String code;
    private Username invited;

    public JbstMongoInvitation(Username owner, Set<SimpleGrantedAuthority> authorities) {
        this.owner = owner;
        this.authorities = authorities;
        this.code = randomStringLetterOrNumbersOnly(JbstInvitation.DEFAULT_INVITATION_CODE_LENGTH);
    }

    public JbstMongoInvitation(JbstInvitation invitation) {
        this.id = invitation.id().value();
        this.owner = invitation.owner();
        this.authorities = invitation.authorities();
        this.code = invitation.code();
        this.invited = invitation.invited();
    }

    public static JbstMongoInvitation admin(String owner) {
        return new JbstMongoInvitation(Username.of(owner), getSimpleGrantedAuthorities("admin"));
    }

    public static JbstMongoInvitation admin(String owner, String value) {
        var invitation = admin(owner);
        invitation.setCode(value);
        return invitation;
    }

    public static JbstMongoInvitation admin(String owner, String value, String invited) {
        var invitation = admin(owner, value);
        invitation.setInvited(Username.of(invited));
        return invitation;
    }

    public static List<JbstMongoInvitation> dummies1() {
        var invitation1 = JbstMongoInvitation.admin("user1");
        var invitation2 = JbstMongoInvitation.admin("user1");
        var invitation3 = JbstMongoInvitation.admin("user2");
        var invitation4 = JbstMongoInvitation.admin("user2");
        var invitation5 = JbstMongoInvitation.admin("user2");
        var invitation6 = JbstMongoInvitation.admin("user3");

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

    public static List<JbstMongoInvitation> dummies2() {
        var invitation1 = JbstMongoInvitation.admin("owner22", "value22");
        var invitation2 = JbstMongoInvitation.admin("owner22", "abc");
        var invitation3 = JbstMongoInvitation.admin("owner22", "value44");
        var invitation4 = JbstMongoInvitation.admin("owner11", "value222");
        var invitation5 = JbstMongoInvitation.admin("owner11", "value111");
        var invitation6 = JbstMongoInvitation.admin("owner33", "value123", "invited1");
        var invitation7 = JbstMongoInvitation.admin("owner34", "value234", "invited2");
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
