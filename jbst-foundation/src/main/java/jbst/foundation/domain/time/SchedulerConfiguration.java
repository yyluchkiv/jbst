package jbst.foundation.domain.time;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;
import static jbst.foundation.utilities.random.RandomUtility.randomLongGreaterThanZeroByBounds;

@SuppressWarnings("unused")
public record SchedulerConfiguration(
        long initialDelay,
        long delay,
        TimeUnit unit
) {
    public static final SchedulerConfiguration EVERY_250_MILLISECONDS = new SchedulerConfiguration(250L, 250L, MILLISECONDS);
    public static final SchedulerConfiguration EVERY_1_SECOND = new SchedulerConfiguration(1L, 1L, SECONDS);
    public static final SchedulerConfiguration EVERY_5_SECONDS = new SchedulerConfiguration(5L, 5L, SECONDS);
    public static final SchedulerConfiguration EVERY_15_SECONDS = new SchedulerConfiguration(15L, 15L, SECONDS);
    public static final SchedulerConfiguration EVERY_30_SECONDS = new SchedulerConfiguration(30L, 30L, SECONDS);
    public static final SchedulerConfiguration EVERY_45_SECONDS = new SchedulerConfiguration(45L, 45L, SECONDS);
    public static final SchedulerConfiguration EVERY_1_MINUTE = new SchedulerConfiguration(1L, 1L, MINUTES);
    public static final SchedulerConfiguration EVERY_5_MINUTES = new SchedulerConfiguration(5L, 5L, MINUTES);
    public static final SchedulerConfiguration EVERY_9_MINUTES = new SchedulerConfiguration(9L, 9L, MINUTES);
    public static final SchedulerConfiguration EVERY_15_MINUTES = new SchedulerConfiguration(15L, 15L, MINUTES);
    public static final SchedulerConfiguration EVERY_30_MINUTES = new SchedulerConfiguration(30L, 30L, MINUTES);
    public static final SchedulerConfiguration EVERY_1_HOUR = new SchedulerConfiguration(1L, 1L, HOURS);
    public static final SchedulerConfiguration EVERY_12_HOURS = new SchedulerConfiguration(12L, 12L, HOURS);

    public static SchedulerConfiguration never() {
        return new SchedulerConfiguration(
                Long.MAX_VALUE,
                Long.MAX_VALUE,
                TimeUnit.DAYS
        );
    }

    public SchedulerConfiguration getDeviatedSchedulerConfiguration(long deviationPercent) {
        var lowerBound = 100 - deviationPercent;
        var upperBound = 100 + deviationPercent;
        var initialDelayAsSeconds = this.unit.toSeconds(this.initialDelay);
        var delaySeconds = this.unit.toSeconds(this.delay);
        var initialDelay = randomLongGreaterThanZeroByBounds(lowerBound * initialDelayAsSeconds, upperBound * initialDelayAsSeconds) / 100;
        var delay = randomLongGreaterThanZeroByBounds(lowerBound * delaySeconds, upperBound * delaySeconds) / 100;
        return new SchedulerConfiguration(initialDelay, delay, TimeUnit.SECONDS);
    }

    public long toSeconds() {
        return this.unit.toSeconds(this.delay);
    }
}
