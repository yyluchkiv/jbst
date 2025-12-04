package jbst.foundation.assistants.userdetails;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.repositories.JbstUsersRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class JbstJwtUserDetailsService implements UserDetailsService {

    // Repository
    protected final JbstUsersRepository usersRepository;

    @Override
    public JbstJwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.usersRepository.loadUserByUsername(Username.of(username));
    }
}
