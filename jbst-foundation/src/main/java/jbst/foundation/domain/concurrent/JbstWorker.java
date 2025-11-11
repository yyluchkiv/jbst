package jbst.foundation.domain.concurrent;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

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

    private final Object lock = new Object();

    protected static final ScheduledExecutorService SES = newSingleThreadScheduledExecutor();
    protected Future<?> future = null;
    protected volatile JbstWorkerState state;
    protected long elapsedSeconds;
    protected final Duration duration;

    protected JbstWorker(Duration duration) {
        this.duration = duration;
        this.elapsedSeconds = 0L;
        this.state = JbstWorkerState.CREATED;
    }

    public abstract void onTick();
    public abstract void onComplete();
    public abstract void start();
    public abstract void stop();

    public final Object getLock() {
        return this.lock;
    }

    @SuppressWarnings("unused")
    public final long getRemainingSeconds() {
        return this.duration.toSeconds() - this.elapsedSeconds;
    }

    @SuppressWarnings("unused")
    public final void switchState() {
        synchronized (this.getLock()) {
            if (this.state.isOperative()) {
                this.stop();
            } else {
                this.start();
            }
        }
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    protected final void cancelFuture() {
        if (nonNull(this.future) && !this.future.isCancelled()) {
            this.future.cancel(false);
        }
    }
}
