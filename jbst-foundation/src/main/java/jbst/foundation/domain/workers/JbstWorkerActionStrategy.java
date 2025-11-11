package jbst.foundation.domain.workers;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public abstract class JbstWorkerActionStrategy {
    private final AtomicInteger counter = new AtomicInteger(0);

    public final void addTick() {
        this.counter.incrementAndGet();
    }

    protected final int getTick() {
        return this.counter.get();
    }

    public abstract boolean isActionRequired();
}
