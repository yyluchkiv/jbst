package jbst.ops.server.slack.services.options.impl;

import jbst.foundation.domain.collections.Partitions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jbst.ops.server.domain.keywords.Operation;
import jbst.ops.server.domain.servers.FileSystemMetadataRow;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageFileSystemTable;
import jbst.ops.server.domain.slack.messages.SlackMessageType;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.domain.slack.teams.SlackTeamEvent;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.slack.messaging.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionFileSystemService;
import jbst.ops.server.utils.MessagesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;
import static jbst.ops.server.constants.OpsConstants.Services.FILE_SYSTEM_SERVICE;
import static jbst.ops.server.domain.servers.FileSystemMetadataRow.PERCENTAGE_REVERSED;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.channelSlackMessage;
import static jbst.ops.server.domain.slack.teams.SlackTeamEvent.channelSlackMessages;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionFileSystemServiceImpl implements OptionFileSystemService {

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;
    // Utilities
    private final MessagesUtils messagesUtils;

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
            this.slackMessagingService.send(
                    channelSlackMessage(
                            slackRequestContext,
                            this.messagesUtils.getResponseInfo(Operation.FS_TABLES)
                    )
            );
            successesRows.sort(PERCENTAGE_REVERSED);
            // WARNING: 25 is practical number is this case as max slack rows to wrap a message
            var partitionsSuccesses = Partitions.ofSize(successesRows, 25);
            partitionsSuccesses.forEach(chuckedMappedRows -> {
                var table = new SlackMessageFileSystemTable(chuckedMappedRows).getValue();
                this.slackMessagingService.send(
                        channelSlackMessage(
                                slackRequestContext,
                                table
                        )
                );
            });
        }

        if (!isEmpty(warningTables)) {
            warningTables.add(0, this.messagesUtils.getResponseWarnings());
            this.slackMessagingService.send(
                    channelSlackMessages(
                            slackRequestContext,
                            warningTables
                    )
            );
        }

        if (isEmpty(successesRows) && isEmpty(warningTables)) {
            List<String> messages = new ArrayList<>();
            messages.add(this.messagesUtils.getResponseInfo(Operation.FS_TABLES));
            messages.add(SlackMessageFileSystemTable.getNoFsTable());
            this.slackMessagingService.send(
                    channelSlackMessages(
                            slackRequestContext,
                            messages
                    )
            );
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
        var status = this.messagesUtils.getServiceMessage(anyPresent, FILE_SYSTEM_SERVICE);
        if (anyPresent) {
            var tables = servers.getValues().stream()
                    .map(SlackMessageFileSystemTable::new)
                    .map(SlackMessageFileSystemTable::getValue)
                    .collect(Collectors.toList());
            tables.add(0, status);
            this.slackMessagingService.send(
                    SlackTeamEvent.events(
                            slackRequestContext,
                            slackMessageType,
                            tables
                    )
            );
        } else {
            this.slackMessagingService.send(
                    new SlackTeamEvent(
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
