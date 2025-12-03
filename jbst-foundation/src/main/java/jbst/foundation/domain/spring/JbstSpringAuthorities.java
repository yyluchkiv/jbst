package jbst.foundation.domain.spring;

import jbst.foundation.domain.constants.JbstConstants;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;

@UtilityClass
public class JbstSpringAuthorities {

    public static List<SimpleGrantedAuthority> getSimpleGrantedAuthorities(List<String> authorities) {
        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(Stream<String> authorities) {
        return authorities.map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(Set<String> authorities) {
        return getSimpleGrantedAuthorities(authorities.stream());
    }

    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(String... authorities) {
        return getSimpleGrantedAuthorities(Stream.of(authorities));
    }

    public static String getResponseInvitationsAuthoritiesAsField(Set<SimpleGrantedAuthority> authorities) {
        if (!isEmpty(authorities)) {
            return authorities.stream().map(SimpleGrantedAuthority::getAuthority).sorted().collect(Collectors.joining(", "));
        } else {
            return JbstConstants.Symbols.DASH;
        }
    }
}
