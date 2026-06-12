package jbst.foundation.incidents.feigns.clients;

import feign.FeignException;
import jbst.foundation.incidents.domain.JbstIncident;
import jbst.foundation.incidents.feigns.definitions.JbstIncidentClientTypeServerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import static jbst.foundation.domain.constants.JbstConstants.Logs.SERVER_OFFLINE;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstIncidentClientTypeServer implements JbstIncidentClientV2 {

    // Definitions
    private final JbstIncidentClientTypeServerDefinition serverDefinition;

    @Override
    public void registerIncident(@NotNull JbstIncident incident) {
        try {
            this.serverDefinition.registerIncident(incident);
        } catch (FeignException ex) {
            LOGGER.error(SERVER_OFFLINE, "incident-server", ex.getMessage());
            incident.print();
        }
    }
}
