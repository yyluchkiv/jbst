package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.constants.JbstConstants;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.plurals.Plurable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public record JbstUser(
        UserId id,
        UserCreationOption creationOption,
        Username username,
        boolean enabled,
        ZoneId zoneId,
        Set<SimpleGrantedAuthority> authorities,
        Email email,
        String name
) implements Plurable<UserId> {

    public static JbstUser hardcoded() {
        return new JbstUser(
                UserId.hardcoded(),
                UserCreationOption.hardcoded(),
                Username.hardcoded(),
                true,
                JbstConstants.ZoneIds.UKRAINE,
                new HashSet<>(),
                Email.hardcoded(),
                "jbst-user"
        );
    }
    @Override
    public UserId getId() {
        return this.id;
    }
}
