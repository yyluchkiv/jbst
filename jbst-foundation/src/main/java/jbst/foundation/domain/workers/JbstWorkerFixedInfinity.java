package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.SchedulerConfiguration;

import java.time.Duration;

public abstract class JbstWorkerFixedInfinity extends JbstWorkerFixed {

    protected JbstWorkerFixedInfinity(SchedulerConfiguration interval) {
        super(Duration.ZERO, interval);
    }

    @Override
    public void onComplete() {
        // ignored on infinity worker
    }
}
