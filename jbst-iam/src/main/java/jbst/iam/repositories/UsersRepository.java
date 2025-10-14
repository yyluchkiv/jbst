package jbst.iam.repositories;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.exceptions.base.UsernameAlreadyExistException;
import jbst.foundation.domain.tuples.TuplePresence;
import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.ids.UserId;
import jbst.iam.domain.jwt.JwtUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.ZoneId;

public interface UsersRepository {
    TuplePresence<JwtUser> isPresent(UserId userId);
    JwtUser loadUserByUsername(Username username) throws UsernameNotFoundException;
    JwtUser findByUsernameAsJwtUserOrNull(Username username);
    JwtUser findByEmailAsJwtUserOrNull(Email email);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    long count();
    void confirmEmail(Email email);
    void resetPassword(Email email, Password password);
    UserId saveAs(JwtUser user);
    UserId saveAs(RequestUserRegistration0 requestUserRegistration0, Password password);
    UserId saveAs(RequestUserRegistration1 requestUserRegistration1, Password password, Invitation invitation);
    JwtUser saveAsOrThrow(UserCreationOption creationOption, Username username, Password password, Email email, ZoneId zoneId) throws UsernameAlreadyExistException;
}
