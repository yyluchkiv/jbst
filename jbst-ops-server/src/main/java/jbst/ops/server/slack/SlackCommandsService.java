package jbst.ops.server.slack;

import jbst.foundation.domain.tuples.TuplePresence;
import jbst.ops.server.domain.slack.commands.SlackOpsCommand;
import jbst.ops.server.domain.slack.commands.SlackRequestCommand;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static java.util.Objects.isNull;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.SPRING_BOOT_ACTUATOR_SERVICE;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackCommandsService {

    // Services
    private final MonitoringService monitoringService;

    public final String getMessage(SlackRequestCommand command) {
        if (!command.isValid() || isNull(command.getCmd())) {
            return SlackOpsCommand.getHelpTable();
        }
        if (command.getCmd().isActuators()) {
            var servers = this.monitoringService.getServersSpringBoot();
            return MessagesUtility.getServiceMessage(servers.isAnyProblemsOnSpringBootActuators(), SPRING_BOOT_ACTUATOR_SERVICE) +
                    NEWLINE +
                    new SlackMessageServersSpringActuatorsTable(servers.getMappedActuatorsResponses()).getValue();
        }
        return SlackOpsCommand.getHelpTable();
    }
}
