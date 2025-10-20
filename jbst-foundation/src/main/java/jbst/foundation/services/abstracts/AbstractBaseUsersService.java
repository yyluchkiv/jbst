package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.RequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.RequestUserUpdate1;
import jbst.foundation.domain.dto.requests.RequestUserUpdate2;
import jbst.foundation.domain.enums.UserCreationOption;
import jbst.foundation.domain.exceptions.base.JbstUsernameAlreadyExistException;
import jbst.foundation.domain.jwt.JwtUser;
import jbst.foundation.domain.security.MagicLinkUserCredentials;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.BaseUsersService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;

@AllArgsConstructor
public abstract class AbstractBaseUsersService implements BaseUsersService {

    // Repository
    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public JwtUser findByEmail(Email email) {
        return this.usersRepository.findByEmailAsJwtUserOrNull(email);
    }

    @Override
    public UsernamePasswordCredentials saveOrGetMagicLinkCredentials(MagicLinkUserCredentials credentials) {
        var email = credentials.userToken().email();
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(email);
        var password = Password.of(randomStringLetterOrNumbersOnly(20));
        var hashPassword = Password.of(this.bCryptPasswordEncoder.encode(password.value()));
        if (isNull(user)) {
            var created = false;
            var index = -1;
            while (!created) {
                var username = (index == -1) ? email.getUsername() : new Username(email.getUsername().value() + index);
                try {
                    user = this.usersRepository.saveAsOrThrow(
                            UserCreationOption.MAGICLINK,
                            username,
                            hashPassword,
                            email,
                            credentials.zoneId()
                    );
                    created = true;
                } catch (JbstUsernameAlreadyExistException ex) {
                    index++;
                }
            }
        }
        // re-save password to avoid BadCredentials in authenticationManager
        this.usersRepository.resetPassword(user.username(), hashPassword);
        return new UsernamePasswordCredentials(user.username(), password);
    }

    @Override
    public void updateUser1(JwtUser user, RequestUserUpdate1 request) {
        user = new JwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                user.password(),
                request.zoneId(),
                user.authorities(),
                request.email(),
                request.name(),
                user.passwordChangeRequired(),
                user.emailDetails(),
                user.attributes()
        );
        this.saveAndReauthenticate(user);
    }

    @Override
    public void updateUser2(JwtUser user, RequestUserUpdate2 request) {
        user = new JwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                user.password(),
                request.zoneId(),
                user.authorities(),
                user.email(),
                request.name(),
                user.passwordChangeRequired(),
                user.emailDetails(),
                user.attributes()
        );
        this.saveAndReauthenticate(user);
    }

    @Override
    public void changePasswordRequired(JwtUser user, RequestUserChangePasswordBasic request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        user = new JwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                Password.of(hashPassword),
                user.zoneId(),
                user.authorities(),
                user.email(),
                user.name(),
                false,
                user.emailDetails(),
                user.attributes()
        );
        this.saveAndReauthenticate(user);
    }

    @Override
    public void changePassword1(JwtUser user, RequestUserChangePasswordBasic request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        user = new JwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                Password.of(hashPassword),
                user.zoneId(),
                user.authorities(),
                user.email(),
                user.name(),
                user.passwordChangeRequired(),
                user.emailDetails(),
                user.attributes()
        );
        this.saveAndReauthenticate(user);
    }

    @Override
    public void resetPassword(RequestUserPasswordReset request) {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(request.token());
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        this.usersRepository.resetPassword(userToken.email(), Password.of(hashPassword));
        userToken = userToken.withUsed(true);
        this.usersTokensRepository.saveAs(userToken);
    }

    // ================================================================================================================
    // PROTECTED METHODS
    // ================================================================================================================
    protected void saveAndReauthenticate(JwtUser jwtUser) {
        this.usersRepository.saveAs(jwtUser);
        var authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
