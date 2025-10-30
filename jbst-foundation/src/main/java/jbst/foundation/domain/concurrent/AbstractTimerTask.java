package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.annotations.JbstModificationBeta;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;
import lombok.Getter;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public abstract class AbstractTimerTask {
    @Getter
    protected volatile TimerTaskState state;

    private final SchedulerConfiguration interval;
    private final TimeAmount duration;

    @Getter
    private long elapsedSeconds;

    private final ScheduledExecutorService scheduledExecutorService = newSingleThreadScheduledExecutor();
    private Future<?> scheduledFuture = null;

    protected AbstractTimerTask(
            SchedulerConfiguration interval,
            TimeAmount duration
    ) {
        this.interval = interval;
        this.duration = duration;
        this.elapsedSeconds = 0;
        this.state = TimerTaskState.CREATED;
    }

    public abstract void onTick();
    public abstract void onComplete();

    // ================================================================================================================
    // PUBLIC METHODS: GETTERS
    // ================================================================================================================
    public final long getRemainingSeconds() {
        return this.duration.toSeconds() - this.elapsedSeconds;
    }

    // ================================================================================================================
    // PUBLIC METHODS: MUTATIONS
    // ================================================================================================================
    public final void switchState() {
        if (this.state.isOperative()) {
            this.stop();
        } else {
            this.start();
        }
    }

    @JbstModificationBeta(releaseVersion = "v1.30")
    public void start() {
        if (this.state.isOperative()) {
            return;
        }
        this.state = TimerTaskState.OPERATIVE;
        this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {
            this.onTick();
            this.elapsedSeconds += this.interval.unit().toSeconds(this.interval.delay());
            if (this.duration.toSeconds() > 0 && this.elapsedSeconds >= this.duration.toSeconds()) {
                this.onComplete();
                this.scheduledFuture.cancel(false);
            }
        }, this.interval.initialDelay(), this.interval.delay(), this.interval.unit());
    }

    @JbstModificationBeta(releaseVersion = "v1.30")
    public void stop() {
        if (!this.state.isOperative()) {
            return;
        }
        this.scheduledFuture.cancel(false);
        this.state = TimerTaskState.STOPPED;
        this.elapsedSeconds = 0;
    }
}
