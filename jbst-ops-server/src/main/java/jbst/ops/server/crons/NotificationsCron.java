package jbst.ops.server.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.IncidentsProcessor;
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
    private final IncidentsProcessor incidentsProcessor;
    private final MonitoringService monitoringService;
    private final NotificationsService notificationsService;

    @Override
    public void processException(Exception ex) {
        this.incidentsProcessor.processIncident(ex);
    }

    @Scheduled(
            cron = "0 0 8,14,21 * * *",
            zone = "Europe/Kyiv"
    )
    public void fsCron() {
        this.executeCron(
                true,
                () -> {
                    this.notificationsService.notifyShow(this.monitoringService.getServers());
                    this.notificationsService.notifyFs(this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata());
                }
        );
    }

    @Scheduled(
            cron = "0 0 * * * *",
            zone = "Europe/Kyiv"
    )
    public void notificationInfrastructureFileSystemAnyProblemsCron() {
        this.executeCron(
                true,
                () -> {
                    var servers = this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata();
                    if (servers.isAnyPresent()) {
                        this.notificationsService.notifyFsFailures(servers);
                    }
                }
        );
    }
}
