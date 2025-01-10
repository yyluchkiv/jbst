package jbst.ops.server.slack.services.options.impl;

import jbst.ops.server.domain.servers.Server;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.domain.servers.Team;
import jbst.ops.server.domain.slack.messages.SlackMessageServerTable;
import jbst.ops.server.domain.slack.messages.SlackMessageServersSpringActuatorsTable;
import jbst.ops.server.domain.slack.requests.SlackRequestContext;
import jbst.ops.server.properties.atomics.SlackTeamChannelCommunication;
import jbst.ops.server.services.MonitoringService;
import jbst.ops.server.slack.SlackMessagingService;
import jbst.ops.server.slack.services.options.OptionMonitoringService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.TWO_NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.*;
import static jbst.ops.server.domain.slack.teams.SlackTeamEventV1.*;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OptionMonitoringServiceImpl implements OptionMonitoringService {

    // Services
    private final MonitoringService monitoringService;
    // Messaging
    private final SlackMessagingService slackMessagingService;

    @Override
    public void sendShow(SlackRequestContext slackRequestContext) {
        var servers = this.monitoringService.getServers();
        this.slackMessagingService.sendAsync(
                channelSlackMessages(
                        slackRequestContext,
                        this.getServersTables(servers)
                )
        );
    }

    @Override
    public void sendShow(SlackRequestContext slackRequestContext, Team team) {
        var servers = this.monitoringService.getServers(team);
        this.slackMessagingService.sendAsync(
                channelSlackMessages(
                        slackRequestContext,
                        this.getServersTables(servers)
                )
        );
    }

    @Override
    public void sendSpringBootActuators(SlackRequestContext slackRequestContext) {
        var servers = this.monitoringService.getServersSpringBoot();
        var message = MessagesUtility.getServiceMessage(servers.isAnyProblemsOnSpringBootActuators(), SPRING_BOOT_ACTUATOR_SERVICE) +
                NEWLINE +
                new SlackMessageServersSpringActuatorsTable(servers.getMappedActuatorsResponses()).getValue();
        this.slackMessagingService.sendAsync(
                channelSlackMessage(
                        slackRequestContext,
                        message
                )
        );
    }

    @Override
    public void sendChanges(SlackRequestContext slackRequestContext, Servers servers) {
        if (servers.isAnyChanges()) {
            var serversHistory = servers.getValues().stream()
                    .map(server -> MessagesUtility.getServerHistoryMessage(server.name().value(), server.upHistory()))
                    .collect(Collectors.joining(NEWLINE));
            this.slackMessagingService.sendAsync(
                    communicationMainSlackMessage(
                            slackRequestContext,
                            MessagesUtility.getServiceHeaderMessage(MONITORING_HISTORY_SERVICE) + NEWLINE + serversHistory
                    )
            );
        }
    }

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
            this.slackMessagingService.sendAsync(
                    communicationMainSlackMessage(
                            slackRequestContext,
                            MessagesUtility.getServiceMessage(false, MONITORING_SERVICE)
                    )
            );
        }
    }

    @Override
    public void sendShowShortOrFailures(SlackRequestContext slackRequestContext, Servers servers, SlackTeamChannelCommunication slackTeamChannelCommunication) {
        var team = slackTeamChannelCommunication.getTeam();
        if (servers.isAnyProblems(team)) {
            var filteredInfrastructure = servers.getServers(team);
            this.slackMessagingService.sendAsync(
                    communicationTeamSlackMessage(
                            slackRequestContext,
                            "<!here>" + TWO_NEWLINE + this.getFailureServersSlackMessage(slackRequestContext, filteredInfrastructure),
                            slackTeamChannelCommunication
                    )
            );
        } else {
            this.slackMessagingService.sendAsync(
                    communicationTeamSlackMessage(
                            slackRequestContext,
                            MessagesUtility.getServiceMessage(false, MONITORING_SERVICE),
                            slackTeamChannelCommunication
                    )
            );
        }
    }

    // ================================================================================================================
    // Private Methods
    // ================================================================================================================
    private List<String> getServersTables(Servers servers) {
        var messages = servers.getMappedValues().values().stream()
                .map(SlackMessageServerTable::new)
                .map(SlackMessageServerTable::getValue)
                .collect(Collectors.toList());
        messages.add(0, MessagesUtility.getServiceMessage(servers.isAnyProblems(), MONITORING_SERVICE));
        return messages;
    }

    private String getFailureServersSlackMessage(SlackRequestContext slackRequestContext, Servers servers) {
        Predicate<Server> slackTeamPredicate = server -> {
            if (slackRequestContext.getSlackTeam().isSmartApps()) {
                return server.team().isSmartApps();
            }
            return slackRequestContext.getSlackTeam().isTech1();
        };
        return MessagesUtility.getServiceMessage(true, MONITORING_SERVICE) +
                NEWLINE +
                new SlackMessageServerTable(servers.getServersFailure(slackTeamPredicate).getValues()).getValue();
    }
}
