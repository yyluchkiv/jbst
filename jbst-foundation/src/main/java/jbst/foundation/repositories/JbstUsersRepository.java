package jbst.foundation.repositories;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.databases.JbstInvitation;
import jbst.foundation.domain.databases.JbstUsers;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration0;
import jbst.foundation.domain.dto.requests.JbstRequestUserRegistration1;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.ids.JbstUserId;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.tuples.TuplePresence;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;

public interface JbstUsersRepository {
    TuplePresence<JbstJwtUser> isPresent(JbstUserId userId);
    JbstJwtUser loadUserByUsername(Username username) throws UsernameNotFoundException;
    JbstJwtUser findByUsernameAsJwtUserOrNull(Username username);
    JbstJwtUser findByEmailAsJwtUserOrNull(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    JbstUsers findUsers();
    JbstUsers findUsersExcept(Username username);
    long count();
    void confirmEmail(Email email);
    void resetPassword(Email email, Password password);
    void resetPassword(Username username, Password password);
    void disable(Username username);
    JbstUserId saveAs(JbstJwtUser user);
    JbstUserId saveAs(JbstRequestUserRegistration0 requestUserRegistration0, Password password);
    JbstUserId saveAs(JbstRequestUserRegistration1 requestUserRegistration1, Password password, JbstInvitation invitation);
    JbstJwtUser saveAsOrThrow(JbstUserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws JbstExceptions.UsernameAlreadyExist;
}
