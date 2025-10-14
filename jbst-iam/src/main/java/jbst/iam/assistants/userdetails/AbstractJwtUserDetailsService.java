package jbst.iam.assistants.userdetails;

import jbst.foundation.domain.jwt.JwtUser;
import jbst.iam.repositories.UsersRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jbst.foundation.domain.base.Username;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractJwtUserDetailsService implements JwtUserDetailsService {

    // Repository
    protected final UsersRepository usersRepository;

    @Override
    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.usersRepository.loadUserByUsername(Username.of(username));
    }
}
