package jbst.foundation.repositories;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.RequestUserRegistration0;
import jbst.foundation.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;

public interface JbstUsersRepository {
    TuplePresence<JwtUser> isPresent(JbstUserId userId);
    JwtUser loadUserByUsername(Username username) throws UsernameNotFoundException;
    JwtUser findByUsernameAsJwtUserOrNull(Username username);
    JwtUser findByEmailAsJwtUserOrNull(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    JbstUsers findUsers();
    JbstUsers findUsersExcept(Username username);
    long count();
    void confirmEmail(Email email);
    void resetPassword(Email email, Password password);
    void resetPassword(Username username, Password password);
    void disable(Username username);
    JbstUserId saveAs(JwtUser user);
    JbstUserId saveAs(RequestUserRegistration0 requestUserRegistration0, Password password);
    JbstUserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation);
    JwtUser saveAsOrThrow(JbstUserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws JbstExceptions.UsernameAlreadyExist;
}
