package jbst.foundation.validators.base;

import jbst.foundation.domain.base.UsernamePasswordCredentials;
import jbst.foundation.domain.dto.requests.RequestMagicLinkToken;
import jbst.foundation.domain.dto.requests.RequestUserLogin;
import jbst.foundation.domain.enums.JbstUserTokenType;
import jbst.foundation.domain.exceptions.authentication.JbstLoginException;
import jbst.foundation.domain.security.MagicLinkUserCredentials;
import jbst.foundation.repositories.JbstUsersTokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.asserts.Asserts.assertNonNullOrThrow;
import static jbst.foundation.domain.strings.JbstMessages.invalidAttribute;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstAuthenticationValidator {
    // Repositories
    private final JbstUsersTokensRepository usersTokensRepository;

    public final UsernamePasswordCredentials validateLoginStandard(RequestUserLogin request) {
        assertNonNullOrThrow(request.username(), invalidAttribute("username"));
        assertNonNullOrThrow(request.password(), invalidAttribute("password"));
        return new UsernamePasswordCredentials(request.username(), request.password());
    }

    public final MagicLinkUserCredentials validateLoginMagicLink(RequestMagicLinkToken request) throws JbstLoginException {
        var userToken = this.usersTokensRepository.findByValueAsAnyOrNull(request.value());
        if (isNull(userToken) || userToken.isInvalid(JbstUserTokenType.MAGICLINK)) {
            throw new JbstLoginException("Invalid magic link token: %s".formatted(request.value()));
        }
        return new MagicLinkUserCredentials(
                userToken,
                request.zoneId()
        );
    }
}
