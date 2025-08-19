package jbst.foundation.domain.concurrent;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import jbst.foundation.domain.exceptions.base.TooManyRequestsException;

import java.time.Duration;

import static java.util.Objects.nonNull;

@SuppressWarnings("UnstableApiUsage")
public class RateLimiter<T> {

    private final LoadingCache<T, com.google.common.util.concurrent.RateLimiter> cache;

    public RateLimiter(
            int requests,
            Duration duration,
            Duration cacheDuration
    ) {
        var permitsPerSecond = calculatePermitsPerSecond(requests, duration);
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheDuration)
                .build(key -> com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond));
    }

    public boolean tryAcquire(T key) {
        var rateLimiter = this.cache.get(key);
        return nonNull(rateLimiter) && rateLimiter.tryAcquire();
    }

    public void acquire(T key) throws TooManyRequestsException {
        if (!this.tryAcquire(key)) {
            throw new TooManyRequestsException();
        }
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected static double calculatePermitsPerSecond(int requests, Duration duration) {
        return (double) requests / duration.getSeconds();
    }
}
