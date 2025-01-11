package jbst.ops.server.slack.services.options.impl;

import jbst.foundation.domain.collections.Partitions;
import jbst.ops.server.domain.servers.FileSystemMetadataRow;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageFileSystemTable;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.slack.teams.SlackTeamEventV1;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jbst.ops.server.constants.OpsConstants.Services.FILE_SYSTEM_SERVICE;
import static jbst.ops.server.domain.servers.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionFileSystemServiceImpl implements OptionFileSystemService {

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;

    @Override
    public void sendFsTables(SlackRequestContext slackRequestContext) {
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
//            this.slackMessagingService.sendAsync(
//                    channelSlackMessage(
//                            slackRequestContext,
//                            Operation.FS_TABLES.getMessage()
//                    )
//            );
            successesRows.sort(PERCENTAGE_REVERSED);
            // WARNING: 25 is practical number is this case as max slack rows to wrap a message
            var partitionsSuccesses = Partitions.ofSize(successesRows, 25);
            partitionsSuccesses.forEach(chuckedMappedRows -> {
                var table = new SlackMessageFileSystemTable(chuckedMappedRows).getValue();
//                this.slackMessagingService.sendAsync(
//                        channelSlackMessage(
//                                slackRequestContext,
//                                table
//                        )
//                );
            });
        }

        if (!isEmpty(warningTables)) {
            warningTables.add(0, MessagesUtility.getResponseWarnings());
//            this.slackMessagingService.sendAsync(
//                    channelSlackMessages(
//                            slackRequestContext,
//                            warningTables
//                    )
//            );
        }

        if (isEmpty(successesRows) && isEmpty(warningTables)) {
            List<String> messages = new ArrayList<>();
            messages.add(SlackMessageFileSystemTable.getNoFsTable());
//            this.slackMessagingService.sendAsync(
//                    channelSlackMessages(
//                            slackRequestContext,
//                            messages
//                    )
//            );
        }
    }

    @Override
    public void sendFsStatusOnSshRequiredAnyProblemsOnFsMetadata(SlackRequestContext slackRequestContext) {
        this.sendFsStatusOrFailures(
                slackRequestContext,
                this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata(),
                SlackMessageType.CHANNEL
        );
    }

    @Override
    public void sendFsStatusOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType) {
        var anyPresent = servers.isAnyPresent();
        var status = MessagesUtility.getServiceMessage(anyPresent, FILE_SYSTEM_SERVICE);
        if (anyPresent) {
            var tables = servers.getValues().stream()
                    .map(SlackMessageFileSystemTable::new)
                    .map(SlackMessageFileSystemTable::getValue)
                    .collect(Collectors.toList());
            tables.add(0, status);
            this.slackMessagingService.sendAsync(
                    SlackTeamEventV1.events(
                            slackRequestContext,
                            slackMessageType,
                            tables
                    )
            );
        } else {
            this.slackMessagingService.sendAsync(
                    new SlackTeamEventV1(
                            slackRequestContext,
                            slackMessageType,
                            status
                    )
            );
        }
    }

    @Override
    public void sendFsFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType) {
        // WARNING: reuse method, fileSystem == OK is not expected at this method
        this.sendFsStatusOrFailures(slackRequestContext, servers, slackMessageType);
    }
}
