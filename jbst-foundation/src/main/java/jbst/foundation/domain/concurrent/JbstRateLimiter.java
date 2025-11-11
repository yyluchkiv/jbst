package jbst.foundation.domain.concurrent;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jbst.foundation.domain.exceptions.base.JbstTooManyRequestsException;

import java.time.Duration;

import static java.util.Objects.nonNull;

@SuppressWarnings("UnstableApiUsage")
public class JbstRateLimiter<T> {

    private final LoadingCache<T, com.google.common.util.concurrent.RateLimiter> cache;

    public JbstRateLimiter(int requests, Duration duration, Duration cacheDuration) {
        var permitsPerSecond = calculatePermitsPerSecond(requests, duration);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheDuration)
                .build(key -> com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond));
    }

    public boolean tryAcquire(T key) {
        var rateLimiter = this.cache.get(key);
        return nonNull(rateLimiter) && rateLimiter.tryAcquire();
    }

    public void acquire(T key) throws JbstTooManyRequestsException {
        if (!this.tryAcquire(key)) {
            throw new JbstTooManyRequestsException();
        }
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected static double calculatePermitsPerSecond(int requests, Duration duration) {
        return (double) requests / duration.getSeconds();
    }
}
