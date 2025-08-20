package jbst.iam.services.abstracts;

import jbst.foundation.domain.exceptions.tokens.UserEmailConfirmException;
import jbst.foundation.domain.time.TimeAmount;
import jbst.foundation.utilities.random.RandomUtility;
import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestUserRegistrationMagicLink;
import jbst.iam.domain.dto.requests.RequestUserToken;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.services.BaseUsersTokensService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.temporal.ChronoUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.utilities.time.TimestampUtility.getFutureRange;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseUsersTokensService implements BaseUsersTokensService {

    // Repositories
    private final UsersTokensRepository usersTokensRepository;
    private final UsersRepository usersRepository;

    // TODO [YYL, MagicLink]
    @Override
    public UserToken magicLink(RequestUserRegistrationMagicLink request) {
        var email = request.email();
        var user = this.usersRepository.findByEmailAsJwtUserOrNull(email);

        if (user == null) {
            LOGGER.warn("Magic link requested for non-existent email: {}", email.value());
            // For security, don't reveal whether email exists
            // return;
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

        LOGGER.debug("Magic link sent to user with email: {}", email.value());
        return magicLinkToken;
    }

    @Override
    public void confirmEmail(String token) throws UserEmailConfirmException {
        var userToken = this.usersTokensRepository.findByValueAsAny(token);
        if (isNull(userToken)) {
            throw UserEmailConfirmException.tokenNotFound();
        }
        this.usersRepository.confirmEmail(userToken.email());
        userToken = userToken.withUsed(true);
        this.usersTokensRepository.saveAs(userToken);
    }

    @Override
    public UserToken saveAs(RequestUserToken request) {
        return this.usersTokensRepository.saveAs(request);
    }

    @Override
    public UserToken getOrCreate(RequestUserToken request) {
        var token = this.usersTokensRepository.findByUserTokenValidOrNull(request);
        if (nonNull(token)) {
            return token;
        } else {
            return this.usersTokensRepository.saveAs(request);
        }
    }
}
