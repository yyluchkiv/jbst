package jbst.foundation.incidents.feigns.clients;

import jbst.foundation.incidents.domain.JbstIncident;

public interface JbstIncidentClientV2 {
    void registerIncident(JbstIncident incident);
}
