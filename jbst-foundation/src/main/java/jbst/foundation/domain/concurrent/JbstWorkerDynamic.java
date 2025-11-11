package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;

import java.util.concurrent.TimeUnit;

public abstract class JbstWorkerDynamic extends JbstWorker {

    public JbstWorkerDynamic(SchedulerConfiguration interval, TimeAmount duration) {
        super(interval, duration);
    }

    /**
     * Must return the next delay in SECONDS before the next tick.
     * Implementations can adjust this value dynamically each cycle.
     */
    protected abstract long nextDelaySeconds();
    protected abstract void onError(Exception ex);

    @Override
    public void start() {
        if (this.state.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.OPERATIVE;
        this.scheduleNext();
    }

    @Override
    public void stop() {
        if (!this.state.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.STOPPED;
        this.cancelFuture();
        this.elapsedSeconds = 0L;
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void scheduleNext() {
        if (!this.state.isOperative()) {
            return;
        }
        var delay = Math.max(1, this.nextDelaySeconds());

        this.future = SES.schedule(() -> {
            try {
                this.onTick();
                this.elapsedSeconds += delay;

                // Stop when duration reached
                if (this.duration.toSeconds() > 0 && this.elapsedSeconds >= this.duration.toSeconds()) {
                    this.onComplete();
                    this.stop();
                    return;
                }

                // Continue scheduling dynamically
                this.scheduleNext();
            } catch (Exception ex) {
                this.onError(ex);
            }
        }, delay, TimeUnit.SECONDS);
    }
}
