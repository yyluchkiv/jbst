package jbst.foundation.validators.base;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.databases.JbstUserToken;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.enums.UserTokenType;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import jbst.foundation.validators.BaseAuthenticationRequestsValidator;
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
    private final JbstUsersTokensRepository usersTokensRepository;

    @Override
    public UsernamePasswordCredentials validateLoginStandard(RequestUserLogin request) {
        assertNonNullOrThrow(request.username(), invalidAttribute("username"));
        assertNonNullOrThrow(request.password(), invalidAttribute("password"));
        return new UsernamePasswordCredentials(request.username(), request.password());
    }

    @Override
    public JbstUserToken validateLoginMagicLink(RequestMagicLinkToken request) throws JbstLoginException {
        var userToken = this.usersTokensRepository.findByValueAsAny(request.value());
        if (isNull(userToken) || userToken.isInvalid(UserTokenType.MAGICLINK)) {
            throw new JbstLoginException("Invalid magic link token: %s".formatted(request.value()));
        }
        return userToken;
    }
}
