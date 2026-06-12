package jbst.foundation.incidents.feigns.definitions;

import feign.Headers;
import feign.RequestLine;
import jbst.foundation.incidents.domain.JbstIncident;
import org.springframework.http.MediaType;

public interface JbstIncidentClientTypeServerDefinition {
    @RequestLine("POST /api/incidents/register")
    @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
    void registerIncident(JbstIncident incident);
}
