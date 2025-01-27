package jbst.ops.server.resources;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.incidents.domain.Incident;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jbst.ops.server.domain.incidents.OpsIncidentEnv;
import jbst.ops.server.services.IncidentsService;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentsResource {

    // Services
    private final IncidentsService incidentsService;

    @PostMapping("/register")
    public void register(@RequestBody Incident incident, HttpServletRequest request) {
        this.incidentsService.registerIncident(incident, new OpsIncidentEnv(request));
    }
}
