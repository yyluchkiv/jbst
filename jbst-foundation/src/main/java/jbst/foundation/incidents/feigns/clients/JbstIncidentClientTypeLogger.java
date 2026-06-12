package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.incidents.domain.JbstIncident;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstIncidentClientTypeLogger implements JbstIncidentClient {
    @Override
    public void registerIncident(@NotNull JbstIncident incident) {
        incident.print();
    }
}
