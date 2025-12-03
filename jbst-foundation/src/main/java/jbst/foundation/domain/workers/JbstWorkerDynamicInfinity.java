package jbst.foundation.domain.workers;

import jbst.foundation.domain.time.JbstSchedulerConfiguration;

import java.time.Duration;

@SuppressWarnings("unused")
public abstract class JbstWorkerDynamicInfinity extends JbstWorkerDynamic {

    protected JbstWorkerDynamicInfinity(JbstSchedulerConfiguration interval) {
        super(Duration.ZERO);
    }

    @Override
    public void onComplete() {
        // ignored on infinity worker
    }
}
