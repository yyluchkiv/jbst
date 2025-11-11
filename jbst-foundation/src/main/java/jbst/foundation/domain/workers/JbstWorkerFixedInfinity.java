package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.SchedulerConfiguration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class JbstWorkerFixedInfinity extends JbstWorkerFixed {

    protected JbstWorkerFixedInfinity(SchedulerConfiguration interval) {
        super(Duration.of(1L, ChronoUnit.FOREVER), interval);
    }

    @Override
    public void onComplete() {
        // ignored on infinite timer task
    }
}
