package jbst.foundation.services;

import jbst.foundation.domain.base.Email;
import jbst.foundation.domain.exceptions.base.JbstTooManyRequestsException;
import jbst.foundation.domain.jwt.JwtUser;

public interface RateLimitsService {
    void acquireMagicLinkOrThrow(Email email) throws JbstTooManyRequestsException;
    void acquireEmailConfirmationOrThrow(JwtUser user) throws JbstTooManyRequestsException;
}
