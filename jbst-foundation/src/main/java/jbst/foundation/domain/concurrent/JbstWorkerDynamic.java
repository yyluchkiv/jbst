package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.time.SchedulerConfiguration;
import jbst.foundation.domain.time.TimeAmount;

public abstract class JbstWorkerDynamic extends JbstWorker {

    protected JbstWorkerDynamic(SchedulerConfiguration interval, TimeAmount duration) {
        super(interval, duration);
    }

}
