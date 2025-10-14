package jbst.foundation.assistants.userdetails;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.repositories.UsersRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJwtUserDetailsService implements JwtUserDetailsService {

    // Repository
    protected final UsersRepository usersRepository;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.usersRepository.loadUserByUsername(Username.of(username));
    }
}
