package jbst.foundation.incidents.events.subscribers;

import jbst.foundation.incidents.domain.Incident;
import org.springframework.context.event.EventListener;

public interface IncidentSubscriber {
    @EventListener
    void onEvent(Incident incident);
}
