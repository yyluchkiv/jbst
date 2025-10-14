package jbst.iam.assistants.userdetails;

import jbst.foundation.domain.jwt.JwtUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface JwtUserDetailsService extends UserDetailsService {
    JwtUser loadUserByUsername(String username) throws UsernameNotFoundException;
}
