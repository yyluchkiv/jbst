package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;
import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;

@JbstDeletionScheduled(version = "1.38")
public abstract class AbstractInfiniteTimerTask extends AbstractTimerTask {

    protected AbstractInfiniteTimerTask(
            SchedulerConfiguration interval
    ) {
        super(
                interval,
                TimeAmount.forever()
        );
    }

    @Override
    public void onComplete() {
        // ignored on infinite timer task
    }
}
