package jbst.foundation.domain.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jbst.foundation.domain.base.Username;

import java.time.ZoneId;
import java.util.Set;

public record JwtTokenCreationParams(
        Username username,
        Set<SimpleGrantedAuthority> authorities,
        ZoneId zoneId
) {
}
