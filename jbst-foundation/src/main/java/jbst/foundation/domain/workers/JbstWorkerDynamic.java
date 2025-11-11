package jbst.foundation.domain.workers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class JbstWorkerDynamic extends JbstWorker {

    public JbstWorkerDynamic(Duration duration) {
        super(duration);
    }

    public abstract Duration getDelay();

    public void onError(Exception ex) {
        this.stop();
    }

    @Override
    public void start() {
        if (this.isOperative()) {
            return;
        }
        this.state = JbstWorkerState.OPERATIVE;
        this.scheduleNext();
    }

    @Override
    public void stop() {
        if (!this.isOperative()) {
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
        if (!this.isOperative()) {
            return;
        }
        var delaySeconds = this.getDelay().toSeconds();
        this.future = SES.schedule(() -> {
            try {
                this.onTick();
                this.elapsedSeconds += delaySeconds;
                if (this.isCompleted()) {
                    this.onComplete();
                    this.stop();
                    return;
                }
                this.scheduleNext();
            } catch (Exception ex) {
                this.onError(ex);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }
}
