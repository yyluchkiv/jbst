package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.plurals.JbstPlurable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public record JbstUser(
        JbstUserId id,
        JbstUserCreationOption creationOption,
        Username username,
        boolean enabled,
        ZoneId zoneId,
        Set<SimpleGrantedAuthority> authorities,
        Email email,
        String name
) implements JbstPlurable<JbstUserId> {

    public static JbstUser hardcoded() {
        return new JbstUser(
                JbstUserId.hardcoded(),
                JbstUserCreationOption.hardcoded(),
                Username.hardcoded(),
                true,
                JbstConstants.ZoneIds.UKRAINE,
                new HashSet<>(),
                Email.hardcoded(),
                "jbst-user"
        );
    }
    @Override
    public JbstUserId getId() {
        return this.id;
    }
}
