package jbst.foundation.incidents.events.subscribers.impl;

import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerCompleted;
import jbst.foundation.incidents.domain.system.IncidentSystemResetServerStarted;
import jbst.foundation.incidents.events.subscribers.IncidentSubscriber;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentSubscriberImpl implements IncidentSubscriber {

    // Clients
    private final IncidentClient incidentClient;

    @Override
    public void onEvent(IncidentSystemResetServerStarted incident) {
        this.onEvent(incident.getPlainIncident());
    }

    @Override
    public void onEvent(IncidentSystemResetServerCompleted incident) {
        this.onEvent(incident.getPlainIncident());
    }

    @Override
    public void onEvent(Incident incident) {
        this.incidentClient.registerIncident(incident);
    }
}
