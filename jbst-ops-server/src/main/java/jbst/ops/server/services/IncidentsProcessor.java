package jbst.ops.server.services;

import jbst.foundation.incidents.domain.Incident;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import jbst.ops.server.properties.OpsProperties;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentsProcessor {
    // Services
    private final IncidentsService incidentsService;
    // Properties
    private final OpsProperties opsProperties;

    @EventListener
    public void onEvent(Incident incident) {
        this.incidentsService.registerIncident(incident, this.opsProperties.getOpsIncidentEnv());
    }

    public final void processIncident(Throwable throwable) {
        var incident = new Incident(throwable);
        this.incidentsService.registerIncident(incident, this.opsProperties.getOpsIncidentEnv());
    }
}
