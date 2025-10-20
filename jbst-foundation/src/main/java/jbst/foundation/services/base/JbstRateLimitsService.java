package jbst.foundation.services.base;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.concurrent.RateLimiter;
import jbst.foundation.domain.exceptions.base.JbstTooManyRequestsException;
import jbst.foundation.domain.jwt.JwtUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstRateLimitsService {
    private final RateLimiter<Username> emailConfirmationRL = new RateLimiter<>(1, Duration.ofMinutes(1), Duration.ofHours(1));
    private final RateLimiter<Email> magicLinkRL = new RateLimiter<>(1, Duration.ofMinutes(1), Duration.ofMinutes(15));

    public final void acquireMagicLinkOrThrow(Email email) throws JbstTooManyRequestsException {
        this.magicLinkRL.acquire(email);
    }

    public final void acquireEmailConfirmationOrThrow(JwtUser user) throws JbstTooManyRequestsException {
        this.emailConfirmationRL.acquire(user.username());
    }
}
