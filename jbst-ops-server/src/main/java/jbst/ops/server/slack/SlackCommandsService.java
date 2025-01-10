package jbst.ops.server.slack;

import jbst.foundation.domain.collections.Partitions;
import jbst.ops.server.domain.keywords.Operation;
import jbst.ops.server.domain.servers.FileSystemMetadataRow;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.commands.SlackOpsCommand;
import jbst.ops.server.domain.slack.commands.SlackRequestCommand;
import jbst.ops.server.domain.slack.messages.SlackMessageFileSystemTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.services.ServersService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.MONITORING_SERVICE;
import static jbst.ops.server.constants.OpsConstants.Services.SPRING_BOOT_ACTUATOR_SERVICE;
import static jbst.ops.server.domain.servers.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SlackCommandsService {

    // Services
    private final ServersService serversService;
    private final MonitoringService monitoringService;

    public final List<String> getMessages(SlackRequestCommand command) {
        List<String> messages = new ArrayList<>();
        if (!command.isValid() || isNull(command.getCmd())) {
            messages.add(SlackOpsCommand.getHelpTable());
        }
        // "ops status"
        if (nonNull(command.getCmd()) && command.getCmd().isStatus()) {
            messages.addAll(this.serversService.getStatus());
        }
        // "ops actuators"
        if (nonNull(command.getCmd()) && command.getCmd().isActuators()) {
            messages.addAll(this.serversService.getActuators());
        }
        // "ops fs"
        if (nonNull(command.getCmd()) && command.getCmd().isFS()) {
            var servers = this.monitoringService.getServersSshRequired();

            List<String> warningTables = new ArrayList<>();
            List<FileSystemMetadataRow> successesRows = new ArrayList<>();

            servers.getValues().forEach(server -> {
                if (server.fileSystemMetadataProblems()) {
                    warningTables.add(new SlackMessageFileSystemTable(server).getValue());
                } else if (server.fileSystemMetadata().isAnyRows()) {
                    successesRows.addAll(server.fileSystemMetadata().rows());
                }
            });

            if (!isEmpty(successesRows)) {
                messages.add(Operation.FS_TABLES.getMessage());
                successesRows.sort(PERCENTAGE_REVERSED);
                // WARNING: 25 is practical number is this case as max slack rows to wrap a message
                var partitionsSuccesses = Partitions.ofSize(successesRows, 25);
                partitionsSuccesses.forEach(chuckedMappedRows -> {
                    var table = new SlackMessageFileSystemTable(chuckedMappedRows).getValue();
                    messages.add(table);
                });
            }

            if (!isEmpty(warningTables)) {
                warningTables.add(0, MessagesUtility.getResponseWarnings());
                messages.addAll(warningTables);
            }

            if (isEmpty(successesRows) && isEmpty(warningTables)) {
                messages.add(Operation.FS_TABLES.getMessage());
                messages.add(SlackMessageFileSystemTable.getNoFsTable());
            }
        }
        return messages;
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
