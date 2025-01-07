package jbst.ops.server.crons;

import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.services.NotificationsService;
import jbst.ops.server.services.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckServersAnyChangesJob {

    // Services
    private final MonitoringService monitoringService;
    // Clients
    private final StateService stateService;
    private final NotificationsService notificationsService;
    // Properties
    private final OpsProperties opsProperties;

    public void checkServersAnyChanges() {
        LOGGER.warn("[Server]: configure check servers any changes job");
        var checkServersAnyChangesJobConfigs = this.opsProperties.getCheckServersAnyChangesJobConfigs();
        if (checkServersAnyChangesJobConfigs.isEnabled()) {
            var configuration = checkServersAnyChangesJobConfigs.getConfiguration();
            Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
                try {
                    if (this.monitoringService.isAnyChanges()) {
                        this.stateService.configure();
                        var servers = this.monitoringService.getServersAnyChanges();
                        this.notificationsService.notifyShowChanges(servers);
                        this.notificationsService.notifyShow(servers);
                        this.notificationsService.notifyShowTeams(servers);
                    }
                } catch (RuntimeException ex) {
                    // no actions
                }
            }, configuration.getInitialDelay(), configuration.getDelay(), configuration.getUnit());
        }
    }
}
