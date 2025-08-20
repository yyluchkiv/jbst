package jbst.iam.validators.base;

import jbst.iam.domain.db.UserToken;
import jbst.iam.domain.dto.requests.RequestMagicLinkToken;
import jbst.iam.domain.dto.requests.RequestUserLogin;
import jbst.iam.domain.enums.UserTokenType;
import jbst.iam.domain.exceptions.LoginException;
import jbst.iam.repositories.UsersRepository;
import jbst.iam.repositories.UsersTokensRepository;
import jbst.iam.validators.BaseAuthenticationRequestsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static jbst.foundation.utilities.exceptions.ExceptionsMessagesUtility.invalidAttribute;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BaseBaseAuthenticationRequestsValidatorImpl implements BaseAuthenticationRequestsValidator {

    // Repositories
    private final UsersTokensRepository usersTokensRepository;

    @Override
    public void validateLoginStandard(RequestUserLogin request) {
        var username = request.username();
        var password = request.password();

        assertNonNullOrThrow(username, invalidAttribute("username"));
        assertNonNullOrThrow(password, invalidAttribute("password"));
    }

    @Override
    public UserToken validateLoginMagicLink(RequestMagicLinkToken request) throws LoginException {
        var userToken = this.usersTokensRepository.findByValueAsAny(request.value());
        if (isNull(userToken) || userToken.isInvalid(UserTokenType.MAGIC_LINK)) {
            throw new LoginException("Invalid magic link token: %s".formatted(request.value()));
        }
        return userToken;
    }
}
