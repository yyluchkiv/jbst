package jbst.server.ops.services;

import jbst.server.ops.domain.incidents.OpsIncident;
import jbst.server.ops.domain.servers.Servers;
import jbst.server.ops.properties.OpsProperties;
import jbst.server.ops.slack.SlackBotsService;
import jbst.server.ops.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.server.ops.constants.OpsConstants.Tasks.HISTORY_TASK;
import static jbst.server.ops.utilities.MessagesUtility.getTaskHeader;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationsService {

    // Services
    private final SlackBotsService slackBotsService;
    // Properties
    private final OpsProperties opsProperties;

    public final void notifyStatus(Servers servers) {
        this.slackBotsService.sendMainBotMainCommunication(servers.getStatus());
    }

    public final void notifyStatusOnTeams(Servers servers) {
        var mappedServers = servers.getMappedValues();
        var sc = this.opsProperties.getSlacksConfigs().getMainSlackConfig();
        sc.getTeamsCommunications().forEach(tc -> {
            if (tc.isOperationalMode()) {
                var teamServers = mappedServers.get(tc.getTeam());
                if (nonNull(teamServers)) {
                    this.slackBotsService.sendMainCommunication(new Servers(teamServers).getStatusShortOrFailures(), tc.getTeam());
                }
            }
        });
    }

    public final void notifyFS(Servers servers) {
        this.slackBotsService.sendMainBotMainCommunication(servers.getFS());
    }

    public final void notifyServersHistory(Servers servers) {
        if (servers.isAnyChanges()) {
            var serversHistory = servers.getValues().stream()
                    .map(server -> MessagesUtility.getServerHistoryMessage(server.name(), server.upHistory()))
                    .collect(Collectors.joining(NEWLINE));
            var message = getTaskHeader(HISTORY_TASK) + NEWLINE + serversHistory;
            this.slackBotsService.sendMainBotMainCommunication(List.of(message));
        }
    }

    public final void notifyIncident(OpsIncident opsIncident) {
        if (opsIncident.getTeam().equals(this.opsProperties.getSlacksConfigs().getMainTeam())) {
            this.slackBotsService.sendMainTeamIncident(opsIncident);
        } else {
            this.slackBotsService.sendIncident(opsIncident);
        }
    }
}
