package jbst.ops.server.slack;

import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.commands.SlackOpsCommand;
import jbst.ops.server.domain.slack.commands.SlackRequestCommand;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.MONITORING_SERVICE;
import static jbst.ops.server.constants.OpsConstants.Services.SPRING_BOOT_ACTUATOR_SERVICE;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackCommandsService {

    // Services
    private final MonitoringService monitoringService;

    public final List<String> getMessages(SlackRequestCommand command) {
        if (!command.isValid() || isNull(command.getCmd())) {
            return List.of(SlackOpsCommand.getHelpTable());
        }
        // "ops status"
        if (command.getCmd().isStatus()) {
            var servers = this.monitoringService.getServers();
            return this.getStatus(servers);
        }
        // "ops actuators"
        if (command.getCmd().isActuators()) {
            var servers = this.monitoringService.getServersSpringBoot();
            var message = MessagesUtility.getServiceMessage(servers.isAnyProblemsOnSpringBootActuators(), SPRING_BOOT_ACTUATOR_SERVICE) +
                    NEWLINE +
                    new SlackMessageServersSpringActuatorsTable(servers.getMappedActuatorsResponses()).getValue();
            return List.of(message);
        }
        return List.of(SlackOpsCommand.getHelpTable());
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
