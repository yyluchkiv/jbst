package jbst.iam.services;

import jbst.foundation.domain.exceptions.base.TooManyRequestsException;
import jbst.iam.domain.jwt.JwtUser;

public interface RateLimitsService {
    void acquireEmailConfirmationOrThrow(JwtUser user) throws TooManyRequestsException;
}
