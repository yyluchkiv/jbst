package jbst.foundation.domain.databases;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.ids.UserId;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.Set;

public record JbstUser(
        UserId id,
        UserCreationOption creationOption,
        Username username,
        ZoneId zoneId,
        Set<SimpleGrantedAuthority> authorities,
        Email email,
        String name,
        boolean enabled
) {
}
