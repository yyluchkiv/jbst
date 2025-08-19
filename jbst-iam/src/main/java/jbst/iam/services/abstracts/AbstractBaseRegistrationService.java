package jbst.iam.services.abstracts;

import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.utilities.random.RandomUtility;
import jbst.iam.domain.db.Invitation;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserRegistration0;
import jbst.iam.domain.dto.requests.RequestUserRegistration1;
import jbst.iam.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.repositories.InvitationsRepository;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.services.BaseRegistrationService;
import jbst.iam.services.UsersEmailsService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jbst.foundation.domain.base.Password;

import java.time.temporal.ChronoUnit;

import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseRegistrationService implements BaseRegistrationService {

    // TODO [YYL, MagicLink] move to resource
    // Services
    private final UsersEmailsService usersEmailsService;
    // Repository
    private final InvitationsRepository invitationsRepository;
    private final UsersRepository usersRepository;
    private final UsersTokensRepository usersTokensRepository;
    // Password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // TODO [YYL, MagicLink]
    @Override
    public void registerMagicLink(RequestUserRegistrationMagicLink request) {
        var email = request.email();
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(email);

        if (user == null) {
            LOGGER.warn("Magic link requested for non-existent email: {}", email.value());
            // For security, don't reveal whether email exists
            return;
        }

        // Create magic link token (15 minutes expiry for security)
        var token = RandomUtility.randomString();
        var magicLinkToken = new UserToken(
                null,
                email,
                token,
                UserTokenType.MAGIC_LINK,
                getFutureRange(new TimeAmount(15, ChronoUnit.MINUTES)).to(),
                false
        );

        // Save token
        this.usersTokensRepository.saveAs(magicLinkToken);

        // Send email with magic link
        this.usersEmailsService.executeMagicLinkEmail(magicLinkToken);

        LOGGER.debug("Magic link sent to user with email: {}", email.value());
    }

    @Override
    public void register0(RequestUserRegistration0 request) {
        var hashPassword = this.bCryptPasswordEncoder.encode(request.password().value());
        this.usersRepository.saveAs(request, Password.of(hashPassword));
    }

    @Override
    public void register1(RequestUserRegistration1 request) {
        var invitation = this.invitationsRepository.findByCodeAsAny(request.code());
        var hashPassword = this.bCryptPasswordEncoder.encode(request.password().value());
        invitation = new Invitation(
                invitation.id(),
                invitation.owner(),
                invitation.authorities(),
                invitation.code(),
                request.username()
        );
        this.usersRepository.saveAs(request, Password.of(hashPassword), invitation);
        this.invitationsRepository.saveAs(invitation);
    }
}
