package jbst.foundation.domain.jwt;

import jbst.foundation.domain.base.Username;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.ZoneId;
import java.util.Set;

public record JbstJwtTokenCreationParams(
        Username username,
        Set<SimpleGrantedAuthority> authorities,
        ZoneId zoneId
) {
}
