package jbst.foundation.domain.workers;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@SuppressWarnings("unused")
public abstract class JbstWorkerActionStrategy {
    private final AtomicInteger counter;
    private final AtomicLong executionStartNanos;

    public JbstWorkerActionStrategy() {
        this.counter = new AtomicInteger(0);
        this.executionStartNanos = new AtomicLong(System.nanoTime());
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

    public final void executionStart() {
        this.executionStartNanos.set(System.nanoTime());
    }

    public final String getExecutionTime() {
        var durationNanos = System.nanoTime() - this.executionStartNanos.get();
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
