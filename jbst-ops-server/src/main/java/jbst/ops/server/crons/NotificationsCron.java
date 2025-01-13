package jbst.ops.server.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.foundation.incidents.domain.Incident;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.IncidentsService;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.services.NotificationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsCron extends AbstractBaseCron {

    // Services
    private final IncidentsService incidentsService;
    private final MonitoringService monitoringService;
    private final NotificationsService notificationsService;
    // Properties
    private final OpsProperties opsProperties;

    @Override
    public void processException(Exception ex) {
        var incident = new Incident(ex);
        this.incidentsService.registerIncident(incident, this.opsProperties.getOpsIncidentEnv());
    }

    @Scheduled(
            cron = "0 0 8,14,21 * * *",
            zone = "Europe/Kyiv"
    )
    public void notifyStatus() {
        this.executeCron(
                true,
                () -> {
                    this.notificationsService.notifyStatus(this.monitoringService.getServers());
                    this.notificationsService.notifyFS(this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata());
                }
        );
    }

    @Scheduled(
            cron = "0 0 * * * *",
            zone = "Europe/Kyiv"
    )
    public void notifyFsFailures() {
        this.executeCron(
                true,
                () -> {
                    var servers = this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata();
                    if (servers.isAnyPresent()) {
                        this.notificationsService.notifyFS(servers);
                    }
                }
        );
    }
}
