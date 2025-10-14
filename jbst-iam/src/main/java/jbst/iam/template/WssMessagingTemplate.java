package jbst.iam.template;

import jbst.foundation.domain.events.WebsocketEvent;
import org.springframework.scheduling.annotation.Async;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;

import java.util.Set;

public interface WssMessagingTemplate {
    @Async
    void sendEventToUser(Username username, String destination, WebsocketEvent event);

    @Async
    void sendHardwareMonitoring(Set<Username> usernames, HardwareMonitoringDatapointTableView tableView);
    @Async
    void sendResetServerStatus(Set<Username> usernames, ResetServerStatus status);
}
