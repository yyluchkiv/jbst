package jbst.server.ops.resources;

import jakarta.servlet.http.HttpServletRequest;
import jbst.foundation.incidents.domain.JbstIncident;
import jbst.server.ops.domain.incidents.OpsIncidentEnv;
import jbst.server.ops.services.IncidentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/incidents")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IncidentsResource {

    // Services
    private final IncidentsService incidentsService;

    @PostMapping("/register")
    public void register(@RequestBody JbstIncident incident, HttpServletRequest request) {
        this.incidentsService.registerIncident(incident, new OpsIncidentEnv(request));
    }
}
