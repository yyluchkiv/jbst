package jbst.iam.domain.db;

import jbst.foundation.domain.ids.InvitationId;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jbst.foundation.domain.base.Username;

import java.util.Set;

import static jbst.foundation.utilities.spring.SpringAuthoritiesUtility.getSimpleGrantedAuthorities;
import static jbst.foundation.domain.base.AbstractAuthority.SUPERADMIN;
import static jbst.foundation.utilities.random.RandomUtility.randomString;

public record Invitation(
        InvitationId id,
        Username owner,
        Set<SimpleGrantedAuthority> authorities,
        String code,
        Username invited
) {

    public static final Sort INVITATION_CODES_UNUSED = Sort.by("owner").ascending()
            .and(Sort.by("code").ascending());

    public static final int DEFAULT_INVITATION_CODE_LENGTH = 40;

    public static Invitation random() {
        return new Invitation(
                InvitationId.random(),
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                Username.random()
        );
    }

    public static Invitation randomNotPersisted() {
        return new Invitation(
                null,
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                Username.random()
        );
    }

    public static Invitation randomNoInvited() {
        return new Invitation(
                InvitationId.random(),
                Username.random(),
                getSimpleGrantedAuthorities(SUPERADMIN),
                randomString(),
                null
        );
    }
}
