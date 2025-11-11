package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;

@SuppressWarnings("unused")
public abstract class JbstWorkerDynamicInfinity extends JbstWorkerDynamic {

    protected JbstWorkerDynamicInfinity(SchedulerConfiguration interval) {
        super(TimeAmount.forever());
    }

    @Override
    public void onComplete() {
        // ignored on infinite timer task
    }
}
