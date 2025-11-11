package jbst.foundation.domain.concurrent;

import jbst.foundation.domain.annotations.JbstDeletionScheduled;

@JbstDeletionScheduled(version = "1.38")
public record TimerTaskPermissions(
        boolean start,
        boolean stop
) {
}
