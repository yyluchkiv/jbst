package jbst.iam.services.base;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.concurrent.RateLimiter;
import jbst.foundation.domain.exceptions.base.TooManyRequestsException;
import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.services.RateLimitsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RateLimitsServiceImpl implements RateLimitsService {
    private final RateLimiter<Username> emailConfirmationRL = new RateLimiter<>(1, Duration.ofMinutes(1), Duration.ofHours(1));
    private final RateLimiter<Email> magicLinkRL = new RateLimiter<>(1, Duration.ofMinutes(1), Duration.ofMinutes(15));

    @Override
    public void acquireMagicLinkOrThrow(Email email) throws TooManyRequestsException {
        this.magicLinkRL.acquire(email);
    }

    @Override
    public void acquireEmailConfirmationOrThrow(JwtUser user) throws TooManyRequestsException {
        this.emailConfirmationRL.acquire(user.username());
    }
}
