package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.ids.InvitationId;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

import static jbst.foundation.domain.base.AbstractAuthority.SUPERADMIN;
import static jbst.foundation.utilities.random.RandomUtility.randomString;
import static jbst.foundation.domain.spring.JbstSpringAuthorities.getSimpleGrantedAuthorities;

public record JbstInvitation(
        InvitationId id,
        Username owner,
        Set<SimpleGrantedAuthority> authorities,
        String code,
        Username invited
) {

    public static final Sort INVITATION_CODES_UNUSED = Sort.by("owner").ascending()
            .and(Sort.by("code").ascending());

    public static final int DEFAULT_INVITATION_CODE_LENGTH = 40;

    public static JbstInvitation random() {
        return new JbstInvitation(
                InvitationId.random(),
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                Username.random()
        );
    }

    public static JbstInvitation randomNotPersisted() {
        return new JbstInvitation(
                null,
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                Username.random()
        );
    }

    public static JbstInvitation randomNoInvited() {
        return new JbstInvitation(
                InvitationId.random(),
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                null
        );
    }
}
