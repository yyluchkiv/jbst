package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;

import java.time.Duration;

public abstract class JbstWorkerFixed extends JbstWorker {

    protected final SchedulerConfiguration interval;

    public JbstWorkerFixed(Duration duration, SchedulerConfiguration interval) {
        super(duration);
        this.interval = interval;
    }

    public void start() {
        if (this.state.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.OPERATIVE;
        this.future = SES.scheduleWithFixedDelay(() -> {
            this.onTick();
            this.elapsedSeconds += this.interval.toSeconds();
            if (this.duration.toSeconds() > 0 && this.elapsedSeconds >= this.duration.toSeconds()) {
                this.onComplete();
                this.cancelFuture();
            }
        }, this.interval.initialDelay(), this.interval.delay(), this.interval.unit());
    }

    public void stop() {
        if (!this.state.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.STOPPED;
        this.cancelFuture();
        this.elapsedSeconds = 0L;
    }
}
