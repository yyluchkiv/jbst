package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.SchedulerConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public abstract class JbstWorkerDynamicInfinity extends JbstWorkerDynamic {

    protected JbstWorkerDynamicInfinity(SchedulerConfiguration interval) {
        super(Duration.of(1L, ChronoUnit.FOREVER));
    }

    @Override
    public void onComplete() {
        // ignored on infinite timer task
    }
}
