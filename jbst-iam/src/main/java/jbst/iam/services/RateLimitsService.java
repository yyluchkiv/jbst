package jbst.iam.services;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.exceptions.base.TooManyRequestsException;
import jbst.foundation.domain.jwt.JwtUser;

public interface RateLimitsService {
    void acquireMagicLinkOrThrow(Email email) throws TooManyRequestsException;
    void acquireEmailConfirmationOrThrow(JwtUser user) throws TooManyRequestsException;
}
