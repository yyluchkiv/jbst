package jbst.foundation.domain.workers;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public abstract class JbstWorker {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum JbstWorkerState {
        CREATED("Created"),
        OPERATIVE("Operative"),
        STOPPED("Stopped");

        private final String value;

        @JsonValue
        public String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @SuppressWarnings("unused")
        public JbstWorkerPermissions getPermissions() {
            return new JbstWorkerPermissions(
                    CREATED.equals(this) || STOPPED.equals(this),
                    this.isOperative()
            );
        }

        public boolean isOperative() {
            return OPERATIVE.equals(this);
        }
    }

    public record JbstWorkerPermissions(boolean start, boolean stop) {}

    /**
     * ReentrantLock instead of synchronized — avoids carrier-thread pinning on virtual threads (Java 21, JEP 444)
     */
    private final ReentrantLock lock = new ReentrantLock();

    protected static final ScheduledExecutorService SES = newSingleThreadScheduledExecutor(Thread.ofVirtual().factory());
    protected Future<?> future = null;
    protected volatile JbstWorkerState state;
    protected long elapsedSeconds;
    /**
     * Duration.ZERO == FOREVER: (this.duration.toSeconds() > 0)
     */
    protected final Duration duration;

    protected JbstWorker(Duration duration) {
        this.duration = duration;
        this.elapsedSeconds = 0L;
        this.state = JbstWorkerState.CREATED;
    }

    public abstract void onTick();
    public abstract void onComplete();
    public abstract void start();

    public final ReentrantLock getLock() {
        return this.lock;
    }

    @SuppressWarnings("unused")
    public final void switchState() {
        this.lock.lock();
        try {
            if (this.isOperative()) {
                this.stop();
            } else {
                this.start();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void stop() {
        if (!this.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.STOPPED;
        this.cancelFuture();
        this.elapsedSeconds = 0L;
    }

    public final boolean isOperative() {
        return this.state.isOperative();
    }

    @SuppressWarnings("unused")
    public final long getRemainingSeconds() {
        return this.duration.toSeconds() - this.elapsedSeconds;
    }

    // =================================================================================================================
    // PROTECTED METHODS
    // =================================================================================================================
    protected final boolean isCompleted() {
        return this.duration.toSeconds() > 0 && this.elapsedSeconds >= this.duration.toSeconds();
    }

    protected final void cancelFuture() {
        if (nonNull(this.future) && !this.future.isCancelled()) {
            this.future.cancel(false);
        }
    }
}
