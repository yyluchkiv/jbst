package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.JbstSchedulerConfiguration;

import java.time.Duration;

public abstract class JbstWorkerFixedInfinity extends JbstWorkerFixed {

    protected JbstWorkerFixedInfinity(JbstSchedulerConfiguration interval) {
        super(Duration.ZERO, interval);
    }

    @Override
    public void onComplete() {
        // ignored on infinity worker
    }
}
