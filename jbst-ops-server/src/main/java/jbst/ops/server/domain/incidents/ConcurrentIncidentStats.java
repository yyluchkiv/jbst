package jbst.ops.server.domain.incidents;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static jbst.foundation.utilities.time.TimestampUtility.getCurrentTimestamp;

// Lombok
@EqualsAndHashCode
@ToString
public class ConcurrentIncidentStats {
    @Getter
    private final OpsIncidentEnv env;
    private final AtomicInteger currentTimes;
    private final AtomicInteger previousTimes;
    private final AtomicLong lastTime;

    public ConcurrentIncidentStats(OpsIncidentEnv env) {
        this.env = env;
        this.currentTimes = new AtomicInteger(1);
        this.previousTimes = new AtomicInteger(1);
        this.lastTime = new AtomicLong(getCurrentTimestamp());
    }

    public void incrementStats() {
        this.currentTimes.incrementAndGet();
        this.lastTime.set(getCurrentTimestamp());
    }

    public long getLastTime() {
        return this.lastTime.get();
    }

    public long getTimes() {
        return this.currentTimes.get();
    }

    public boolean isExecutedMoreThanOnce() {
        return this.currentTimes.get() > 1;
    }

    public boolean getExecutedTimesDifferenceFlagAndUpdatePreviousIfIncidentRegistrationRequired() {
        var difference = this.currentTimes.get() - this.previousTimes.get();
        var registerIncident = difference > 10;
        if (registerIncident) {
            this.previousTimes.addAndGet(difference);
        }
        return registerIncident;
    }
}
