package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.SchedulerConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public abstract class JbstWorkerDynamicInfinity extends JbstWorkerDynamic {

    protected JbstWorkerDynamicInfinity(SchedulerConfiguration interval) {
        super(Duration.ZERO);
    }

    @Override
    public void onComplete() {
        // ignored on infinity worker
    }
}
