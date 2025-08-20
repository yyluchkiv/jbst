package jbst.iam.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.exceptions.base.UsernameAlreadyExistException;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.*;
import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.services.BaseUsersService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.util.Objects.isNull;
import static jbst.foundation.utilities.random.RandomUtility.randomStringLetterOrNumbersOnly;

@AllArgsConstructor
public abstract class AbstractBaseUsersService implements BaseUsersService {

    // Repository
    private final UsersTokensRepository usersTokensRepository;
    private final UsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public JwtUser findByEmail(Email email) {
        return this.usersRepository.findByEmailAsJwtUserOrNull(email);
    }

    @Override
    public void safeCreateMagicLinkUser(UserToken userToken, RequestMagicLinkToken request) {
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(userToken.email());
        if (isNull(user)) {
            var created = false;
            var index = -1;
            var password = Password.of(this.bCryptPasswordEncoder.encode(randomStringLetterOrNumbersOnly(20)));
            while (!created) {
                var username = (index == -1) ? userToken.email().getUsername() : new Username(userToken.email().getUsername().value() + index);
                try {
                    user = this.usersRepository.saveAsMagicLinkOrThrow(
                            username,
                            password,
                            userToken,
                            request
                    );
                    created = true;
                } catch (UsernameAlreadyExistException ex) {
                    index++;
                }
            }
        }
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
        var userToken = this.usersTokensRepository.findByValueAsAny(request.token());
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
