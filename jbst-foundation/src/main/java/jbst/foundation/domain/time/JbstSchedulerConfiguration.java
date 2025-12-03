package jbst.foundation.domain.time;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;
import static jbst.foundation.domain.random.JbstRandom.randomLongGreaterThanZeroByBounds;

@SuppressWarnings("unused")
public record JbstSchedulerConfiguration(
        long initialDelay,
        long delay,
        TimeUnit unit
) {
    public static final JbstSchedulerConfiguration EVERY_250_MILLISECONDS = new JbstSchedulerConfiguration(250L, 250L, MILLISECONDS);
    public static final JbstSchedulerConfiguration EVERY_1_SECOND = new JbstSchedulerConfiguration(1L, 1L, SECONDS);
    public static final JbstSchedulerConfiguration EVERY_5_SECONDS = new JbstSchedulerConfiguration(5L, 5L, SECONDS);
    public static final JbstSchedulerConfiguration EVERY_15_SECONDS = new JbstSchedulerConfiguration(15L, 15L, SECONDS);
    public static final JbstSchedulerConfiguration EVERY_30_SECONDS = new JbstSchedulerConfiguration(30L, 30L, SECONDS);
    public static final JbstSchedulerConfiguration EVERY_45_SECONDS = new JbstSchedulerConfiguration(45L, 45L, SECONDS);
    public static final JbstSchedulerConfiguration EVERY_1_MINUTE = new JbstSchedulerConfiguration(1L, 1L, MINUTES);
    public static final JbstSchedulerConfiguration EVERY_5_MINUTES = new JbstSchedulerConfiguration(5L, 5L, MINUTES);
    public static final JbstSchedulerConfiguration EVERY_9_MINUTES = new JbstSchedulerConfiguration(9L, 9L, MINUTES);
    public static final JbstSchedulerConfiguration EVERY_15_MINUTES = new JbstSchedulerConfiguration(15L, 15L, MINUTES);
    public static final JbstSchedulerConfiguration EVERY_30_MINUTES = new JbstSchedulerConfiguration(30L, 30L, MINUTES);
    public static final JbstSchedulerConfiguration EVERY_1_HOUR = new JbstSchedulerConfiguration(1L, 1L, HOURS);
    public static final JbstSchedulerConfiguration EVERY_12_HOURS = new JbstSchedulerConfiguration(12L, 12L, HOURS);

    public static JbstSchedulerConfiguration never() {
        return new JbstSchedulerConfiguration(
                Long.MAX_VALUE,
                Long.MAX_VALUE,
                TimeUnit.DAYS
        );
    }

    public JbstSchedulerConfiguration getDeviatedSchedulerConfiguration(long deviationPercent) {
        var lowerBound = 100 - deviationPercent;
        var upperBound = 100 + deviationPercent;
        var initialDelayAsSeconds = this.unit.toSeconds(this.initialDelay);
        var delaySeconds = this.unit.toSeconds(this.delay);
        var initialDelay = randomLongGreaterThanZeroByBounds(lowerBound * initialDelayAsSeconds, upperBound * initialDelayAsSeconds) / 100;
        var delay = randomLongGreaterThanZeroByBounds(lowerBound * delaySeconds, upperBound * delaySeconds) / 100;
        return new JbstSchedulerConfiguration(initialDelay, delay, TimeUnit.SECONDS);
    }

    public long toSeconds() {
        return this.unit.toSeconds(this.delay);
    }
}
