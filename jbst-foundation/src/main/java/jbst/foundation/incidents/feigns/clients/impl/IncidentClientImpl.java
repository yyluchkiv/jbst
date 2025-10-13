package jbst.foundation.incidents.feigns.clients.impl;

import feign.FeignException;
import jbst.foundation.incidents.domain.Incident;
import jbst.foundation.incidents.feigns.clients.IncidentClient;
import jbst.foundation.incidents.feigns.definitions.IncidentClientDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static jbst.foundation.domain.constants.JbstConstants.Logs.SERVER_OFFLINE;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentClientImpl implements IncidentClient {

    // Definitions
    private final IncidentClientDefinition incidentClientDefinition;

    @Override
    public void registerIncident(Incident incident) {
        try {
            this.incidentClientDefinition.registerIncident(incident);
        } catch (FeignException ex) {
            LOGGER.error(SERVER_OFFLINE, "incident-server", ex.getMessage());
            incident.print();
        }
    }
}
