package jbst.ops.server.services;

import jbst.ops.server.domain.incidents.OpsIncident;
import jbst.ops.server.domain.servers.Servers;
import jbst.ops.server.properties.OpsProperties;
import jbst.ops.server.slack.SlackBotsService;
import jbst.ops.server.utilities.MessagesUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static jbst.foundation.domain.constants.JbstConstants.Symbols.NEWLINE;
import static jbst.ops.server.constants.OpsConstants.Services.HISTORY_SERVICE;
import static jbst.ops.server.utilities.MessagesUtility.getTaskHeader;

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

    // TODO [YYL] fixme
    public final void notifyStatusOnTeams(Servers servers) {
//        var tcs = this.opsProperties.getTech1SlackConfigs().getTeamsCommunications();
//        tcs.stream()
//                .filter(tcc -> tcc.getCommunication().isEnabled())
//                .filter(tcc -> servers.isAnyChanges(tcc.getTeam()))
//                .forEach(tcc -> {
//                    var team = tcc.getTeam();
//
//                    // parent slack - Tech1: `any` team
//                    this.optionMonitoringService.sendShowShortOrFailures(
//                            limitedTech1(),
//                            servers,
//                            tcc
//                    );
//
//                    // child slack - SmartApps: SmartApps team
//                    if (team.isSmartApps()) {
//                        this.optionMonitoringService.sendShowShortOrFailures(
//                                limitedSmartApps(),
//                                servers
//                        );
//                    }
//                });
    }

    public final void notifyFS(Servers servers) {
        this.slackBotsService.sendMainBotMainCommunication(servers.getFS());
    }

    public final void notifyServersHistory(Servers servers) {
        if (servers.isAnyChanges()) {
            var serversHistory = servers.getValues().stream()
                    .map(server -> MessagesUtility.getServerHistoryMessage(server.name(), server.upHistory()))
                    .collect(Collectors.joining(NEWLINE));
            var message = getTaskHeader(HISTORY_SERVICE) + NEWLINE + serversHistory;
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
