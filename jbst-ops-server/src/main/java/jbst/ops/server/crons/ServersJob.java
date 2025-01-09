package jbst.ops.server.crons;

import jbst.foundation.domain.properties.base.SchedulerConfiguration;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.services.NotificationsService;
import jbst.ops.server.services.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServersJob {
    private static final SchedulerConfiguration SC = new SchedulerConfiguration(0L, 30L, SECONDS);

    // Services
    private final MonitoringService monitoringService;
    // Clients
    private final StateService stateService;
    private final NotificationsService notificationsService;

    public final void scheduleAnyChangesNotification() {
        newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
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
        }, SC.getInitialDelay(), SC.getDelay(), SC.getUnit());
    }
}
