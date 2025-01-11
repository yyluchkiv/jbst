package jbst.ops.server.slack.services.options.impl;

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

import java.util.stream.Collectors;

import static jbst.ops.server.constants.OpsConstants.Services.FILE_SYSTEM_SERVICE;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionFileSystemServiceImpl implements OptionFileSystemService {

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;

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
