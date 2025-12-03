package jbst.foundation.services.base;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.concurrent.JbstRateLimiter;
import jbst.foundation.domain.exceptions.JbstExceptions;
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
    private final JbstRateLimiter<Email> magicLinkRL = new JbstRateLimiter<>(5, Duration.ofMinutes(1), Duration.ofMinutes(10));
    private final JbstRateLimiter<Username> emailConfirmationRL = new JbstRateLimiter<>(1, Duration.ofMinutes(1), Duration.ofHours(1));

    public final void acquireMagicLinkOrThrow(Email email) throws JbstExceptions.TooManyRequests {
        this.magicLinkRL.acquire(email);
    }

    public final void acquireEmailConfirmationOrThrow(JwtUser user) throws JbstExceptions.TooManyRequests {
        this.emailConfirmationRL.acquire(user.username());
    }
}
