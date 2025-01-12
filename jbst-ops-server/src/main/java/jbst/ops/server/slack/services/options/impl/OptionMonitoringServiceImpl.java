package jbst.ops.server.slack.services.options.impl;

import jbst.ops.server.domain.servers.Server;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.STATUS_SERVICE;
import static jbst.ops.server.domain.slack.teams.SlackTeamEventV1.communicationMainSlackMessage;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionMonitoringServiceImpl implements OptionMonitoringService {

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;

    @Override
    public void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers) {
        if (servers.isAnyProblems()) {
            this.slackMessagingService.sendAsync(
                    communicationMainSlackMessage(
                            slackRequestContext,
                            this.getFailureServersSlackMessage(slackRequestContext, servers)
                    )
            );
        } else {
//            this.slackMessagingService.sendAsync(
//                    communicationMainSlackMessage(
//                            slackRequestContext,
//                            MessagesUtility.getServiceMessage(false, MONITORING_SERVICE)
//                    )
//            );
        }
    }

//    @Override
//    public void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackTeamChannelCommunication slackTeamChannelCommunication) {
//        var team = slackTeamChannelCommunication.getTeam();
//        if (servers.isAnyProblems(team)) {
//            var filteredInfrastructure = servers.getServers(team);
//            this.slackMessagingService.sendAsync(
//                    communicationTeamSlackMessage(
//                            slackRequestContext,
//                            "<!here>" + TWO_NEWLINE + this.getFailureServersSlackMessage(slackRequestContext, filteredInfrastructure),
//                            slackTeamChannelCommunication
//                    )
//            );
//        } else {
//            this.slackMessagingService.sendAsync(
//                    communicationTeamSlackMessage(
//                            slackRequestContext,
//                            MessagesUtility.getServiceMessage(false, MONITORING_SERVICE),
//                            slackTeamChannelCommunication
//                    )
//            );
//        }
//    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private String getFailureServersSlackMessage(SlackRequestContext slackRequestContext, Servers servers) {
        Predicate<Server> slackTeamPredicate = server -> {
            if (slackRequestContext.getSlackTeam().isSmartApps()) {
                // TODO [YYL] fixme
//                return server.team().isSmartApps();
            }
            return slackRequestContext.getSlackTeam().isTech1();
        };
        // TODO [YYL] STATUS_SERVICE?
        return MessagesUtility.getServiceMessageV1(true, STATUS_SERVICE) +
                NEWLINE +
                new SlackMessageServerTable(servers.getServersFailure(slackTeamPredicate).getValues()).getValue();
    }
}
