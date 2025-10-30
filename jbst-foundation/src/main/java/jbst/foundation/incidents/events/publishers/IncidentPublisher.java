package jbst.foundation.incidents.events.publishers;

import jbst.foundation.incidents.domain.Incident;

public interface IncidentPublisher {
    void publishIncident(Incident incident);

    default void publishThrowable(Throwable throwable) {
        this.publishIncident(new Incident(throwable));
    }
}
