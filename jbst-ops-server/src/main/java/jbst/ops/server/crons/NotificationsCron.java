package jbst.ops.server.crons;

import jbst.foundation.domain.crons.AbstractBaseCron;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.IncidentsProcessor;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.services.NotificationsService;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsCron extends AbstractBaseCron {

    // Services
    private final IncidentsProcessor incidentsProcessor;
    private final MonitoringService monitoringService;
    private final NotificationsService notificationsService;
    // Properties
    private final OpsProperties opsProperties;

    @Override
    public void processException(Exception ex) {
        this.incidentsProcessor.processIncident(ex);
    }

    @Scheduled(
            cron = "${ops-configs.crons-configs.servers-or-fs-notification-cron.expression}",
            zone = "${ops-configs.crons-configs.servers-or-fs-notification-cron.zone-id}"
    )
    public void serversCron() {
        this.executeCron(
                this.opsProperties.getCronsConfigs().getServersOrFsNotificationCron().isEnabled(),
                () -> {
                    var servers = this.monitoringService.getServers();
                    this.notificationsService.notifyShow(servers);
                }
        );
    }

    @Scheduled(
            cron = "${ops-configs.crons-configs.servers-or-fs-notification-cron.expression}",
            zone = "${ops-configs.crons-configs.servers-or-fs-notification-cron.zone-id}"
    )
    public void fsCron() {
        this.executeCron(
                this.opsProperties.getCronsConfigs().getServersOrFsNotificationCron().isEnabled(),
                () -> {
                    var servers = this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata();
                    this.notificationsService.notifyFs(servers);
                }
        );
    }

    @Scheduled(
            cron = "${ops-configs.crons-configs.fs-any-problems-notification-cron.expression}",
            zone = "${ops-configs.crons-configs.fs-any-problems-notification-cron.zone-id}"
    )
    public void notificationInfrastructureFileSystemAnyProblemsCron() {
        this.executeCron(
                this.opsProperties.getCronsConfigs().getFsAnyProblemsNotificationCron().isEnabled(),
                () -> {
                    var servers = this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata();
                    if (servers.isAnyPresent()) {
                        this.notificationsService.notifyFsFailures(servers);
                    }
                }
        );
    }
}
