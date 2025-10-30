package jbst.foundation.incidents.feigns.definitions;

import jbst.foundation.incidents.domain.Incident;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class JbstIncidentClientDefinitionSlf4J implements JbstIncidentClientDefinition {

    @Override
    public void registerIncident(@NotNull Incident incident) {
        incident.print();
    }
}
