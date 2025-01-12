package jbst.ops.server.slack.services.options.impl;

import jbst.ops.server.slack.services.options.OptionFileSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionFileSystemServiceImpl implements OptionFileSystemService {

//    @Override
//    public void sendFsStatusOnSshRequiredAnyProblemsOnFsMetadata(SlackRequestContext slackRequestContext) {
//        this.sendFsStatusOrFailures(
//                slackRequestContext,
//                this.monitoringService.getServersSshRequiredAnyProblemsOnFsMetadata(),
//                SlackMessageType.CHANNEL
//        );
//    }
//
//    @Override
//    public void sendFsStatusOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType) {
//        var anyPresent = servers.isAnyPresent();
//        var status = MessagesUtility.getServiceMessageV1(anyPresent, FILE_SYSTEM_SERVICE);
//        if (anyPresent) {
//            var tables = servers.getValues().stream()
//                    .map(SlackMessageFileSystemTable::new)
//                    .map(SlackMessageFileSystemTable::getValue)
//                    .collect(Collectors.toList());
//            tables.add(0, status);
//            this.slackMessagingService.sendAsync(
//                    SlackTeamEventV1.events(
//                            slackRequestContext,
//                            slackMessageType,
//                            tables
//                    )
//            );
//        } else {
//            this.slackMessagingService.sendAsync(
//                    new SlackTeamEventV1(
//                            slackRequestContext,
//                            slackMessageType,
//                            status
//                    )
//            );
//        }
//    }
//
//    @Override
//    public void sendFsFailures(SlackRequestContext slackRequestContext, Servers servers, SlackMessageType slackMessageType) {
//        // WARNING: reuse method, fileSystem == OK is not expected at this method
//        this.sendFsStatusOrFailures(slackRequestContext, servers, slackMessageType);
//    }
}
