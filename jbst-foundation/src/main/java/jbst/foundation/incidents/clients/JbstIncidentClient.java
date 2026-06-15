package jbst.foundation.incidents.clients;

import jbst.foundation.incidents.domain.JbstIncident;
import org.jetbrains.annotations.NotNull;

public interface JbstIncidentClient {
    void registerIncident(@NotNull JbstIncident incident);
}
