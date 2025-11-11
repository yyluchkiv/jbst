package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.TimeAmount;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class JbstWorkerDynamic extends JbstWorker {

    public JbstWorkerDynamic(TimeAmount duration) {
        super(duration);
    }

    public abstract Duration getDelay();
    public abstract void onError(Exception ex);

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
        this.future = SES.schedule(() -> {
            try {
                this.onTick();
                this.elapsedSeconds += this.getDelay().toSeconds();

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
        }, this.getDelay().toSeconds(), TimeUnit.SECONDS);
    }
}
