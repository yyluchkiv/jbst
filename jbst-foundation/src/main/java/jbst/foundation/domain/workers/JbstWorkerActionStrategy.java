package jbst.foundation.domain.workers;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@SuppressWarnings("unused")
public abstract class JbstWorkerActionStrategy {
    private final AtomicInteger counter;
    private final long startNanos;

    public JbstWorkerActionStrategy() {
        this.counter = new AtomicInteger(0);
        this.startNanos = System.nanoTime();
    }
    // =================================================================================================================
    // ABSTRACTION
    // =================================================================================================================
    public abstract boolean isActionRequired();

    // =================================================================================================================
    // GETTERS
    // =================================================================================================================
    public final int getTick() {
        return this.counter.get();
    }

    // =================================================================================================================
    // MUTATIONS
    // =================================================================================================================
    public final void addTick() {
        this.counter.incrementAndGet();
    }

    public final String getExecutedTime() {
        var durationNanos = System.nanoTime() - this.startNanos;
        var seconds = NANOSECONDS.toSeconds(durationNanos);
        if (seconds < 60) {
            return seconds + "s";
        }
        var minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m";
        }
        var hours = minutes / 60;
        return hours + "h";
    }
}
