package jbst.foundation.websockets;

import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.events.WebsocketEvent;
import jbst.foundation.domain.hardware.monitoring.HardwareMonitoringDatapointTableView;
import jbst.foundation.domain.properties.JbstProperties;
import jbst.foundation.domain.system.reset_server.ResetServerStatus;
import jbst.foundation.incidents.services.JbstIncidentsPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

import static jbst.foundation.domain.events.WebsocketEvent.hardwareMonitoring;
import static jbst.foundation.domain.events.WebsocketEvent.resetServerProgress;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JbstWebsocketsService {

    private final SimpMessagingTemplate messagingTemplate;
    private final JbstIncidentsPublisher incidentsPublisher;
    private final JbstProperties jbstProperties;

    public final void sendEventToUser(Username username, String destination, WebsocketEvent event) {
        this.sendObjectToUser(
                username,
                destination,
                event
        );
    }

    public final void sendHardwareMonitoring(Set<Username> usernames, HardwareMonitoringDatapointTableView tableView) {
        var hardwareConfigs = this.jbstProperties.getSecurity().getWebsockets().getFeaturesConfigs().getHardwareConfigs();
        if (!hardwareConfigs.isEnabled()) {
            return;
        }
        usernames.forEach(username -> this.sendObjectToUser(username, hardwareConfigs.getUserDestination(), hardwareMonitoring(tableView)));
    }

    public final void sendResetServerStatus(Set<Username> usernames, ResetServerStatus status) {
        var resetServerConfigs = this.jbstProperties.getSecurity().getWebsockets().getFeaturesConfigs().getResetServerConfigs();
        if (!resetServerConfigs.isEnabled()) {
            return;
        }
        usernames.forEach(username -> this.sendObjectToUser(username, resetServerConfigs.getUserDestination(), resetServerProgress(status)));
    }

    // =================================================================================================================
    // PRIVATE METHODS
    // =================================================================================================================
    private void sendObjectToUser(Username username, String destination, Object data) {
        if (!this.jbstProperties.getSecurity().getWebsockets().isEnabled()) {
            return;
        }
        var brokerConfigs = this.jbstProperties.getSecurity().getWebsockets().getBrokerConfigs();
        try {
            this.messagingTemplate.convertAndSendToUser(
                    username.value(),
                    brokerConfigs.getSimpleDestination() + destination,
                    data
            );
        } catch (MessagingException ex) {
            this.incidentsPublisher.publishThrowable(ex);
        }
    }
}
