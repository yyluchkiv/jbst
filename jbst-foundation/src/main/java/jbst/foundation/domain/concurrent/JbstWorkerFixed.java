package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;

public abstract class JbstWorkerFixed extends JbstWorker {

    protected JbstWorkerFixed(SchedulerConfiguration interval, TimeAmount duration) {
        super(interval, duration);
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
                this.future.cancel(false);
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
