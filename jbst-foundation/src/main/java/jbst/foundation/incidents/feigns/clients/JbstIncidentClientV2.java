package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.incidents.domain.JbstIncident;
import org.jetbrains.annotations.NotNull;

public interface JbstIncidentClientV2 {
    void registerIncident(@NotNull JbstIncident incident);
}
