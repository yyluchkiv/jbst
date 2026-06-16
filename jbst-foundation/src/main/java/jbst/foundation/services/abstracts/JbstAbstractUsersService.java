package jbst.foundation.services.abstracts;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Password;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.JbstRequestUserChangePasswordBasic;
import jbst.foundation.domain.dto.requests.JbstRequestUserPasswordReset;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate1;
import jbst.foundation.domain.dto.requests.JbstRequestUserUpdate2;
import jbst.foundation.domain.enums.JbstUserCreationOption;
import jbst.foundation.domain.exceptions.JbstExceptions;
import jbst.foundation.domain.jwt.JbstJwtUser;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.security.JbstMagicLinkUserCredentials;
import jbst.foundation.repositories.JbstUsersRepository;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.services.JbstUsersService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.JbstAsserts.assertTrueOrThrow;
import static jbst.foundation.domain.constants.JbstConstants.Logs.PREFIX;
import static jbst.foundation.domain.enums.JbstStatus.COMPLETED;
import static jbst.foundation.domain.random.JbstRandom.randomStringLetterOrNumbersOnly;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@AllArgsConstructor
public abstract class JbstAbstractUsersService implements JbstUsersService {

    // Repository
    private final JbstUsersTokensRepository usersTokensRepository;
    private final JbstUsersRepository usersRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    // Properties
    private final JbstProperties jbstProperties;

    @Override
    public void initUsers() {
        var essenceConfigs = this.jbstProperties.getSecurity().getEssence();
        assertTrueOrThrow(
                essenceConfigs.getUsersOnInit().isEnabled(),
                invalidAttribute("essence-configs.users-on-init.enabled == true")
        );
        if (this.usersRepository.count() == 0L) {
            LOGGER.info("{} essence 'users-on-init' — adding users to database", PREFIX);
            var usersCount = this.initUsers(essenceConfigs.getUsersOnInit().getUsers());
            LOGGER.info("{} essence 'users-on-init' — saved users: {}", PREFIX, usersCount);
        }
        LOGGER.info("{} essence 'users-on-init' — {}", PREFIX, COMPLETED.asANSI());
    }

    @Override
    public JbstJwtUser findByEmail(Email email) {
        return this.usersRepository.findByEmailAsJwtUserOrNull(email);
    }

    @Override
    public UsernamePasswordCredentials saveOrGetMagicLinkCredentials(JbstMagicLinkUserCredentials credentials) {
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
                            JbstUserCreationOption.MAGICLINK,
                            username,
                            hashPassword,
                            email,
                            credentials.zoneId()
                    );
                    created = true;
                } catch (JbstExceptions.UsernameAlreadyExist ex) {
                    index++;
                }
            }
        }
        // re-save password to avoid BadCredentials in authenticationManager
        this.usersRepository.resetPassword(user.username(), hashPassword);
        return new UsernamePasswordCredentials(user.username(), password);
    }

    @Override
    public void updateUser1(JbstJwtUser user, JbstRequestUserUpdate1 request) {
        user = new JbstJwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                user.password(),
                user.enabled(),
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
    public void updateUser2(JbstJwtUser user, JbstRequestUserUpdate2 request) {
        user = new JbstJwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                user.password(),
                user.enabled(),
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
    public void changePasswordRequired(JbstJwtUser user, JbstRequestUserChangePasswordBasic request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        user = new JbstJwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                Password.of(hashPassword),
                user.enabled(),
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
    public void changePassword1(JbstJwtUser user, JbstRequestUserChangePasswordBasic request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        user = new JbstJwtUser(
                user.id(),
                user.creationOption(),
                user.username(),
                Password.of(hashPassword),
                user.enabled(),
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
    public void resetPassword(JbstRequestUserPasswordReset request) {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(request.token());
        var hashPassword = this.bCryptPasswordEncoder.encode(request.newPassword().value());
        this.usersRepository.resetPassword(userToken.email(), Password.of(hashPassword));
        userToken = userToken.withUsed(true);
        this.usersTokensRepository.saveAs(userToken);
    }

    // ================================================================================================================
    // PROTECTED METHODS
    // ================================================================================================================
    protected void saveAndReauthenticate(JbstJwtUser jwtUser) {
        this.usersRepository.saveAs(jwtUser);
        var authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
