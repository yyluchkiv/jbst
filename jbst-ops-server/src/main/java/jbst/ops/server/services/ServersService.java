package jbst.ops.server.services;

import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.MONITORING_SERVICE;
import static jbst.ops.server.constants.OpsConstants.Services.SPRING_BOOT_ACTUATOR_SERVICE;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ServersService {

    // Services
    private final MonitoringService monitoringService;

    public final List<String> getStatus() {
        var servers = this.monitoringService.getServers();
        return this.getStatus(servers);
    }

    public final List<String> getActuators() {
        var servers = this.monitoringService.getServersSpringBoot();
        var message = MessagesUtility.getServiceMessage(servers.isAnyProblemsOnSpringBootActuators(), SPRING_BOOT_ACTUATOR_SERVICE) +
                NEWLINE +
                new SlackMessageServersSpringActuatorsTable(servers.getMappedActuatorsResponses()).getValue();
        return List.of(message);
    }

    // ================================================================================================================
    // PRIVATE METHODS
    // ================================================================================================================
    private List<String> getStatus(Servers servers) {
        var messages = servers.getMappedValues().values().stream()
                .map(SlackMessageServerTable::new)
                .map(SlackMessageServerTable::getValue)
                .collect(Collectors.toList());
        messages.add(0, MessagesUtility.getServiceMessage(servers.isAnyProblems(), MONITORING_SERVICE));
        return messages;
    }
}
