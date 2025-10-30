package jbst.foundation.incidents.feigns.definitions;

import feign.Headers;
import feign.RequestLine;
import jbst.foundation.incidents.domain.Incident;
import org.springframework.http.MediaType;

public interface JbstIncidentClientDefinition {
    @RequestLine("POST /api/incidents/register")
    @Headers("Content-Type: " + MediaType.APPLICATION_JSON_VALUE)
    void registerIncident(Incident incident);
}
