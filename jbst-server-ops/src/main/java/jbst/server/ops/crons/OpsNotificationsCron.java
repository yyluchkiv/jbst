package jbst.server.ops.crons;

import jbst.foundation.domain.crons.JbstAbstractCron;
import jbst.foundation.incidents.domain.Incident;
import jbst.server.ops.properties.ServerProperties;
import jbst.server.ops.services.IncidentsService;
import jbst.server.ops.services.MonitoringService;
import jbst.server.ops.services.NotificationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OpsNotificationsCron extends JbstAbstractCron {

    // Services
    private final IncidentsService incidentsService;
    private final MonitoringService monitoringService;
    private final NotificationsService notificationsService;
    // Properties
    private final ServerProperties serverProperties;

    @Override
    public void processException(Exception ex) {
        var incident = new Incident(ex);
        this.incidentsService.registerIncident(incident, this.serverProperties.getOpsIncidentEnv());
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
