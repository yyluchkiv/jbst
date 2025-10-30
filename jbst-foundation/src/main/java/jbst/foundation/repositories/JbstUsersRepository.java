package jbst.foundation.repositories;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUser;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.exceptions.base.JbstUsernameAlreadyExistException;
import jbst.foundation.domain.ids.UserId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;
import java.util.List;

public interface JbstUsersRepository {
    TuplePresence<JwtUser> isPresent(UserId userId);
    JwtUser loadUserByUsername(Username username) throws UsernameNotFoundException;
    JwtUser findByUsernameAsJwtUserOrNull(Username username);
    JwtUser findByEmailAsJwtUserOrNull(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    List<JbstUser> findUsersExcept(Username username);
    long count();
    void confirmEmail(Email email);
    void resetPassword(Email email, Password password);
    void resetPassword(Username username, Password password);
    void disable(Username username);
    UserId saveAs(JwtUser user);
    UserId saveAs(RequestUserRegistration0 requestUserRegistration0, Password password);
    UserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation);
    JwtUser saveAsOrThrow(UserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws JbstUsernameAlreadyExistException;
}
