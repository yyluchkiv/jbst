package jbst.ops.server.slack;

import jbst.ops.server.domain.slack.commands.SlackOpsCommand;
import jbst.ops.server.domain.slack.commands.SlackRequestCommand;
import jbst.ops.server.services.MonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackCommandsService {

    // Services
    private final MonitoringService monitoringService;

    public final List<String> getMessages(SlackRequestCommand command) {
        List<String> messages = new ArrayList<>();
        if (!command.isValid() || isNull(command.getCmd())) {
            messages.add(SlackOpsCommand.getHelpTable());
        }
        // "ops status"
        if (nonNull(command.getCmd()) && command.getCmd().isStatus()) {
            messages.addAll(this.monitoringService.getServers().getStatus());
        }
        // "ops actuators"
        if (nonNull(command.getCmd()) && command.getCmd().isActuators()) {
            messages.addAll(this.monitoringService.getServersSpringBoot().getActuators());
        }
        // "ops fs"
        if (nonNull(command.getCmd()) && command.getCmd().isFS()) {
            messages.addAll(this.monitoringService.getServersSshRequired().getFS());
        }
        return messages;
    }
}
